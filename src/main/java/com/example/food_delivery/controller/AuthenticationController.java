package com.example.food_delivery.controller;

import com.example.food_delivery.dto.request.LoginRequest;
import com.example.food_delivery.dto.request.SignupRequest;
import com.example.food_delivery.core.response.ApiResponse;
import com.example.food_delivery.dto.response.AuthenticationResponse;
import com.example.food_delivery.dto.response.UserDTO;
import com.example.food_delivery.service.AuthenticationService;
import com.example.food_delivery.service.imp.AuthenticationServiceImp;
import com.example.food_delivery.service.imp.UserServiceImp;
import com.nimbusds.jose.JOSEException;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationController {

    AuthenticationServiceImp authenticationServiceImp;
    UserServiceImp userService;

    @PostMapping("/signing")
    ApiResponse signing(@RequestBody LoginRequest loginRequest) {
        ApiResponse apiResponse = new ApiResponse();
        var result = authenticationServiceImp.checkLogin(loginRequest);
        apiResponse.setResult(result);
        return apiResponse;
    }

    @PostMapping("/signup")
    ApiResponse signup(@RequestBody @Valid SignupRequest signupRequest) {
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setResult(userService.addUser(signupRequest));

        return apiResponse;
    }

    @PostMapping("/introspect")
    ApiResponse introspect(@RequestBody @Valid com.example.food_delivery.dto.request.IntrospectRequest request)
            throws ParseException, JOSEException {

        var result = authenticationServiceImp.introspect(request);
        var resp = new ApiResponse();
        resp.setResult(result);
        return resp;
    }

    @PostMapping("/logout")
    ApiResponse logout(@RequestBody @Valid com.example.food_delivery.dto.request.LogoutRequest request) throws ParseException, JOSEException {

        authenticationServiceImp.logout(request);

        return new ApiResponse();
    }

    @PostMapping("/refresh")
    ApiResponse refresh(@RequestBody com.example.food_delivery.dto.request.RefreshRequest request)
            throws ParseException, JOSEException {

        var result = authenticationServiceImp.refreshToken(request);

        var resp = new ApiResponse();
        resp.setResult(result);
        return resp;
    }
}
