package com.example.food_delivery.service;

import com.example.food_delivery.domain.entity.Category;
import com.example.food_delivery.domain.entity.Food;
import com.example.food_delivery.reponsitory.FoodRepository;
import com.example.food_delivery.reponsitory.RestaurantReponsitory;
import com.example.food_delivery.service.imp.FileServiceImp;
import com.example.food_delivery.service.imp.MenuServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class MenuService implements MenuServiceImp {
    @Autowired
    FileServiceImp fileServiceImp;
    @Autowired
    FoodRepository foodRepository;
    @Autowired
    private RestaurantReponsitory restaurantReponsitory;

    @Override
    public boolean createMenu(MultipartFile file, String title, String time_ship, String is_freeship, Double price, int cate_id) {
        boolean result = false; //check variable

        try{
            boolean isSaveFileSuccess = fileServiceImp.saveFile(file);
            if(isSaveFileSuccess){
                Food food = new Food();
                food.setTitle(title);
                food.setImage(file.getOriginalFilename());
                food.setTime_ship(time_ship);
                food.setPrice(price);

                Category category = new Category();
                category.setId(cate_id);
                food.setCategory(category);

                foodRepository.save(food);

                result = true;
            }
        }catch(Exception e){
            System.out.println("Error in insert food: " + e.getMessage());
        }

        return result;
    }


}
