package com.example.food_delivery.service;

import com.example.food_delivery.domain.entity.Food;
import com.example.food_delivery.domain.entity.Restaurant;
import com.example.food_delivery.domain.entity.Users;
import com.example.food_delivery.reponsitory.FoodRepository;
import com.example.food_delivery.reponsitory.RestaurantReponsitory;
import com.example.food_delivery.reponsitory.UserReponsitory;
import com.example.food_delivery.service.imp.SearchServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchService implements SearchServiceImp {

    @Autowired
    RestaurantReponsitory restaurantRepository;

    @Autowired
    FoodRepository foodRepository;

    @Autowired
    UserReponsitory userRepository;

    @Override
    public Map<String, Object> searchAll(String keyword) {
        Map<String, Object> results = new HashMap<>();
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                results.put("restaurants", new ArrayList<>());
                results.put("foods", new ArrayList<>());
                results.put("users", new ArrayList<>());
                return results;
            }

            String searchKeyword = keyword.trim();
            results.put("restaurants", searchRestaurants(searchKeyword));
            results.put("foods", searchFoods(searchKeyword));
            results.put("users", searchUsers(searchKeyword));
            return results;
        } catch (Exception e) {
            System.err.println("Error in searchAll: " + e.getMessage());
            e.printStackTrace();
            results.put("restaurants", new ArrayList<>());
            results.put("foods", new ArrayList<>());
            results.put("users", new ArrayList<>());
            return results;
        }
    }

    @Override
    public List<Restaurant> searchRestaurants(String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return new ArrayList<>();
            }
            return restaurantRepository.searchRestaurants(keyword.trim());
        } catch (Exception e) {
            System.err.println("Error searching restaurants: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<Food> searchFoods(String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return new ArrayList<>();
            }
            return foodRepository.searchFoods(keyword.trim());
        } catch (Exception e) {
            System.err.println("Error searching foods: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<Users> searchUsers(String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return new ArrayList<>();
            }
            return userRepository.searchUsers(keyword.trim());
        } catch (Exception e) {
            System.err.println("Error searching users: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<Restaurant> searchRestaurantsAdvanced(String keyword, String address, Boolean isFreeship) {
        try {
            List<Restaurant> restaurants = searchRestaurants(keyword);
            
            // Apply filters
            if (address != null && !address.trim().isEmpty()) {
                restaurants.removeIf(r -> r.getAddress() == null || 
                    !r.getAddress().toLowerCase().contains(address.toLowerCase()));
            }
            
            if (isFreeship != null) {
                restaurants.removeIf(r -> r.isFreeship() != isFreeship);
            }
            
            return restaurants;
        } catch (Exception e) {
            System.err.println("Error in advanced restaurant search: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<Food> searchFoodsAdvanced(String keyword, Integer categoryId, Double minPrice, Double maxPrice) {
        try {
            List<Food> foods = searchFoods(keyword);
            
            // Apply filters
            if (categoryId != null && categoryId > 0) {
                foods.removeIf(f -> f.getCategory() == null || f.getCategory().getId() != categoryId);
            }
            
            if (minPrice != null && minPrice > 0) {
                foods.removeIf(f -> f.getPrice() < minPrice);
            }
            
            if (maxPrice != null && maxPrice > 0) {
                foods.removeIf(f -> f.getPrice() > maxPrice);
            }
            
            return foods;
        } catch (Exception e) {
            System.err.println("Error in advanced food search: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}

