package com.example.food_delivery.controller;

import com.example.food_delivery.payload.ResponseData;
import com.example.food_delivery.dto.request.SignupRequest;
import com.example.food_delivery.service.LoginService;
import com.example.food_delivery.service.imp.LoginServiceImp;
import com.example.food_delivery.utils.JWTUtilsHelper;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/login")
public class LoginController {
    @Autowired
    LoginServiceImp loginServiceImp;

    @Autowired
    JWTUtilsHelper jwtUtilsHelper;

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestParam String username, @RequestParam String password) {
        ResponseData  responseData = new ResponseData();
        if(loginServiceImp.checkLogin(username, password)){
            String token = jwtUtilsHelper.generateToken(username);
            responseData.setCode(200);
            responseData.setData(token);

        }else {
            responseData.setCode(400);// error code
            responseData.setData("");
            responseData.setSuccess(false);
        }

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest signUpRequest) {
        ResponseData  responseData = new ResponseData();

        responseData.setCode(200);
        responseData.setData(loginServiceImp.addUser(signUpRequest));

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }
}
