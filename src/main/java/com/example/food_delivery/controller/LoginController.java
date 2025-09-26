package com.example.food_delivery.controller;

import com.example.food_delivery.payload.ResponseData;
import com.example.food_delivery.payload.request.SignUpRequest;
import com.example.food_delivery.service.LoginService;
import com.example.food_delivery.service.imp.LoginServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
public class LoginController {
    @Autowired
    LoginServiceImp loginServiceImp;
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestParam String username, @RequestParam String password) {
        ResponseData  responseData = new ResponseData();
        if(loginServiceImp.checkLogin(username, password)){
            responseData.setCode(200);
            responseData.setData(true);
        }else {
//            responseData.setCode(400);
            responseData.setData(false);
        }

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignUpRequest signUpRequest) {
        ResponseData  responseData = new ResponseData();

        responseData.setCode(200);
        responseData.setData(loginServiceImp.addUser(signUpRequest));

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }
}
