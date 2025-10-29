package com.example.food_delivery.service;

import com.example.food_delivery.domain.entity.*;
import com.example.food_delivery.domain.entity.keys.KeyOrderItem;
import com.example.food_delivery.dto.request.OrderRequest;
import com.example.food_delivery.reponsitory.OrderItemRepository;
import com.example.food_delivery.reponsitory.OrderRepository;
import com.example.food_delivery.service.imp.OrderServiceImp;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderService implements OrderServiceImp {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Transactional
    @Override
    public boolean insertOrder(OrderRequest orderRequest) {

        try {

            Restaurant restaurant = new Restaurant();
            restaurant.setId(orderRequest.getResId());

            Users users = new Users();
            users.setId(orderRequest.getUserId());

            Orders orders = new Orders();
            orders.setUsers(users);
            orders.setRestaurant(restaurant);
            orders.setCreateDate(new Date());
            orderRepository.save(orders);

            List<OrderItem> orderItems = new ArrayList<>();
            for (int foodId : orderRequest.getFoodIds()) {

                Food food = new Food();
                food.setId(foodId);

                OrderItem orderItem = new OrderItem();
                KeyOrderItem keyOrderItem = new KeyOrderItem(orders.getId(), foodId);
                orderItem.setKeyOrderItem(keyOrderItem);
                orderItem.setCreateDate(new Date());
                orderItems.add(orderItem);
            }
            orderItemRepository.saveAll(orderItems);

            return true;
        } catch (Exception e) {
            System.out.println("Error insert order" + e);
            return false;
        }
    }
}
