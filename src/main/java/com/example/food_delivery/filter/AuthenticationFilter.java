package com.example.food_delivery.filter;

import com.example.food_delivery.domain.model.AuthenticationToken;
import com.example.food_delivery.domain.model.TokenPayload;
import com.example.food_delivery.exception.AppException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.food_delivery.service.TokenService;

import java.io.IOException;

@Component
@Slf4j
@AllArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {
    private final String HEADER_AUTHORIZATION = "Authorization";
    private final TokenService tokenService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = extractTokenFromRequest(request);

            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }

            TokenPayload payload = tokenService.validateAccessToken(token);
            AuthenticationToken authentication = new AuthenticationToken(payload);
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
        } catch (AppException e) {
            log.error("AppException | {}", e.getMessage());
        }

        filterChain.doFilter(request, response);

    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HEADER_AUTHORIZATION);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}
