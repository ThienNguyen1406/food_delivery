package com.example.food_delivery.controller;

import com.example.food_delivery.dto.request.OrderRequest;
import com.example.food_delivery.payload.ResponseData;
import com.example.food_delivery.service.imp.OrderServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderServiceImp orderService;

    @PostMapping()
    public ResponseEntity<?> insertOrder(@RequestBody OrderRequest orderRequest) {
        ResponseData responseData = new ResponseData();
        responseData.setData(orderService.insertOrder(orderRequest));
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }
}
