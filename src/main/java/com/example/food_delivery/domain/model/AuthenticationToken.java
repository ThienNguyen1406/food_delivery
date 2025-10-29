package com.example.food_delivery.domain.model;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class AuthenticationToken extends AbstractAuthenticationToken {

    private final TokenPayload payload;

    public AuthenticationToken(TokenPayload payload) {
        super(null);
        this.payload = payload;
        this.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return payload;
    }
}
