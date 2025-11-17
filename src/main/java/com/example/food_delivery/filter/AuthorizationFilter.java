package com.example.food_delivery.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

@Component
@Slf4j
public class AuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        
        // Check if endpoint requires admin role
        if (requiresAdminRole(path)) {
            // Check authentication from SecurityContext (set by CustomJwtFilter)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            log.debug("AuthorizationFilter checking path: {} - Authentication: {} - Authenticated: {}", 
                path, 
                authentication != null ? authentication.getName() : "null",
                authentication != null ? authentication.isAuthenticated() : false);
            
            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("Access denied to {} - Not authenticated (auth={}, isAuthenticated={})", 
                    path, 
                    authentication != null ? authentication.getName() : "null",
                    authentication != null ? authentication.isAuthenticated() : false);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Unauthorized: Authentication required\"}");
                return;
            }

            // Check if user has ADMIN role
            if (!isAdmin(authentication)) {
                log.warn("Access denied to {} - User {} does not have ADMIN role", path, authentication.getName());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Forbidden: Admin role required\"}");
                return;
            }
            
            log.info("Access granted to {} - User {} has ADMIN role", path, authentication.getName());
        }

        filterChain.doFilter(request, response);
    }

    private boolean requiresAdminRole(String path) {
        // All /admin/* endpoints are handled by @PreAuthorize in controllers
        // Don't double-check here to avoid conflicts
        if (path.startsWith("/admin/")) {
            return false; // Let @PreAuthorize handle it
        }
        
        // Exclude GET endpoints that are public
        // Allow /restaurant/file/** for public access
        if (path.startsWith("/restaurant/file/")) {
            return false; // Public access for restaurant images
        }
        if (path.startsWith("/restaurant") && !path.startsWith("/restaurant/detail") && path.length() > 11) {
            // Only require admin for POST, PUT, DELETE on /restaurant
            return true;
        }
        if (path.startsWith("/category") && !path.startsWith("/admin/category") && path.length() > 10) {
            // Only require admin for POST, PUT, DELETE on /category (not /admin/category)
            return true;
        }
        // Allow /menu/file/** for public access
        if (path.startsWith("/menu/file/")) {
            return false; // Public access for menu images
        }
        if (path.startsWith("/menu") && path.length() > 5) {
            // Only require admin for POST, PUT, DELETE on /menu
            return true;
        }
        // User endpoints always require admin
        if (path.startsWith("/user") && !path.equals("/user/me")) {
            return true;
        }
        return false;
    }

    private boolean isAdmin(Authentication authentication) {
        if (authentication == null) {
            return false;
        }
        
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities == null || authorities.isEmpty()) {
            log.warn("User {} has no authorities", authentication.getName());
            return false;
        }
        
        // Check if user has ROLE_ADMIN authority
        boolean isAdmin = authorities.stream()
                .anyMatch(authority -> {
                    String authorityString = authority.getAuthority();
                    log.debug("Checking authority: {}", authorityString);
                    return authorityString.equals("ROLE_ADMIN") || authorityString.equals("ADMIN");
                });
        
        log.info("User {} has ADMIN role: {}", authentication.getName(), isAdmin);
        return isAdmin;
    }
}
