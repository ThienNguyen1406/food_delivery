package com.example.food_delivery.service.imp;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

public interface MenuServiceImp {
    boolean createMenu( MultipartFile file, String  title, String time_ship , String is_freeShip, Double price ,int cate_id);
}
