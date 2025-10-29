package com.example.food_delivery.service.imp;


import com.example.food_delivery.domain.model.TokenPayload;
import com.example.food_delivery.exception.AppException;

public interface TokenServiceImp {
    String generateAccessToken(TokenPayload payload);

    TokenPayload validateAccessToken(String token) throws AppException;
}
