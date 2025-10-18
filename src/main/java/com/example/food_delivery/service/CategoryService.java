package com.example.food_delivery.service;

import com.example.food_delivery.dto.CategoryDTO;
import com.example.food_delivery.dto.MenuDTO;
import com.example.food_delivery.entity.Category;
import com.example.food_delivery.entity.Food;
import com.example.food_delivery.reponsitory.CategoryRepository;
import com.example.food_delivery.service.imp.CategoryServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService implements CategoryServiceImp {
    @Autowired
    CategoryRepository categoryRepository;

    @Override
    public List<CategoryDTO> getCategoryHomePage() {
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by("id"));
        List<Category> listCategory = categoryRepository.findAll(pageRequest).getContent();
        List<CategoryDTO> listCategoryDTO = new ArrayList<>();

        for (Category data : listCategory) {
            CategoryDTO categoryDTO = new CategoryDTO();
            categoryDTO.setName(data.getNameCate());

            List<MenuDTO> menuDTOS = new ArrayList<>();
            for(Food dataFood : data.getLisFood()){
                MenuDTO menuDTO = new MenuDTO();
                menuDTO.setTitle(dataFood.getTitle());
                menuDTO.setFreeShip(dataFood.isFreeShip());
                menuDTO.setImage(dataFood.getImage());

                menuDTOS.add(menuDTO);
            }
            categoryDTO.setMenus(menuDTOS);
            listCategoryDTO.add(categoryDTO);
        }

        return listCategoryDTO;
    }
}
