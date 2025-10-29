package com.example.food_delivery.service.imp;

import com.example.food_delivery.dto.request.OrderRequest;

public interface OrderServiceImp {
    boolean insertOrder(OrderRequest orderRequest);
}
