package com.example.food_delivery.service.imp;

import com.example.food_delivery.dto.UserDTO;
import com.example.food_delivery.payload.request.SignUpRequest;

import java.util.List;

public interface LoginServiceImp {
    List<UserDTO> getAllUsers();
    Boolean checkLogin(String username, String password);
    Boolean addUser(SignUpRequest signUpRequest);

}
