package com.example.food_delivery.domain.model;

import com.example.food_delivery.domain.entity.Roles;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@Data
public class TokenPayload {
    private String id;
    private Long userId;
    private Instant issuedAt;
    private Instant expiredAt;
    private List<Roles> roles;
    public TokenPayload(String id, Long userId) {
        this.id = id;
        this.userId = userId;
    }
}
