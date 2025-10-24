package com.example.food_delivery.service.imp;

import com.example.food_delivery.dto.CategoryDTO;
import com.example.food_delivery.entity.Category;

import java.util.List;

public interface CategoryServiceImp {
    List<CategoryDTO> getCategoryHomePage();
}
