package com.example.food_delivery.configuration;

import com.example.food_delivery.filter.AuthorizationFilter;
import com.example.food_delivery.security.CustomJwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    private static final String[] PUBLIC_ENDPOINTS = {
        "/auth/**",
        "/login/**",
        "/roles/**",
        "/permissions/**",
        // "/order/**", // Removed - orders should require authentication
        "/restaurant", // GET restaurant list for public (exact match)
        "/restaurant/**", // GET restaurant detail and images for public
        "/category", // GET categories for public (exact match)
        "/category/**", // Category endpoints for public
        "/menu/file/**", // GET menu images for public
        "/search/**", // Search endpoints for public
        "/promo/**", // Promo endpoints for public
        "/theme/**", // Frontend theme-sidebar files
        "/admin/**", // Admin panel files (includes /admin/**.html)
        "/", // Root - serve index.html
        "/*.html", // HTML files at root
        "/js/**", // JavaScript files
        "/css/**", // CSS files
        "/img/**", // Image files
        "/vendor/**", // Vendor files
        "/font/**" // Font files
    };

    private final AuthorizationFilter authorizationFilter;
    private final CustomJwtFilter customJwtFilter;
    private final CorsConfigurationSource corsConfigurationSource;
    
    public SecurityConfiguration(
            AuthorizationFilter authorizationFilter,
            CustomJwtFilter customJwtFilter,
            CorsConfigurationSource corsConfigurationSource) {
        this.authorizationFilter = authorizationFilter;
        this.customJwtFilter = customJwtFilter;
        this.corsConfigurationSource = corsConfigurationSource;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(
                        request -> request
                                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                                .anyRequest().authenticated())
                // Use CustomJwtFilter to verify Nimbus JWT tokens (created by AuthenticationService)
                // IMPORTANT: CustomJwtFilter must run BEFORE AuthorizationFilter to set authentication
                // Add CustomJwtFilter first (before UsernamePasswordAuthenticationFilter)
                .addFilterBefore(customJwtFilter, UsernamePasswordAuthenticationFilter.class)
                // Add AuthorizationFilter after CustomJwtFilter (before UsernamePasswordAuthenticationFilter)
                // This ensures CustomJwtFilter sets authentication before AuthorizationFilter checks it
                .addFilterAfter(authorizationFilter, CustomJwtFilter.class);



        return httpSecurity.build();
    }


    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
