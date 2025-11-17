package com.example.food_delivery.service;

import com.example.food_delivery.dto.response.CategoryDTO;
import com.example.food_delivery.dto.response.MenuDTO;
import com.example.food_delivery.dto.response.RestaurantDTO;
import com.example.food_delivery.domain.entity.Food;
import com.example.food_delivery.domain.entity.MenuRestaurant;
import com.example.food_delivery.domain.entity.RatingRestaurant;
import com.example.food_delivery.domain.entity.Restaurant;
import com.example.food_delivery.reponsitory.RestaurantReponsitory;
import com.example.food_delivery.service.imp.FileServiceImp;
import com.example.food_delivery.service.imp.RestaurantServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Optional;

@Service
public class RestaurantService implements RestaurantServiceImp {
    @Autowired
    RestaurantReponsitory restaurantReponsitory;
    @Autowired
    FileServiceImp fileServiceImp;

    @Override
    public boolean insertRestaurant(MultipartFile file, String title, String subtitle, String description, boolean is_freeship, String address, String open_date) {
       boolean result = false; //check variable

        try{
            boolean isInsertFileSuccess = fileServiceImp.saveFile(file);
            if(isInsertFileSuccess){
                Restaurant restaurant = new Restaurant();
                restaurant.setTitle(title);
                restaurant.setSubtitle(subtitle);
                restaurant.setDescription(description);
                restaurant.setImage(file.getOriginalFilename());
                restaurant.setFreeship(is_freeship);
                restaurant.setAddress(address);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                Date openDate = simpleDateFormat.parse(open_date);
                restaurant.setOpenDate(openDate);

                restaurantReponsitory.save(restaurant);
                result = true;
            }
        }catch(Exception e){
            System.out.println("Error in insert restaurant: " + e.getMessage());
        }

        return result;
    }

    @Override
    public List<RestaurantDTO> getHomePageRestaurant() {
        List<RestaurantDTO> restaurantDTOS = new ArrayList<>();
        PageRequest  pageRequest = PageRequest.of(0, 6);
        Page<Restaurant> listData = restaurantReponsitory.findAll(pageRequest);

        for(Restaurant restaurant : listData){
            RestaurantDTO restaurantDTO = new RestaurantDTO();
            restaurantDTO.setId(restaurant.getId());
            // Convert image filename to full URL
            if (restaurant.getImage() != null && !restaurant.getImage().isEmpty()) {
                restaurantDTO.setImage("/restaurant/file/" + restaurant.getImage());
            } else {
                restaurantDTO.setImage(restaurant.getImage());
            }
            restaurantDTO.setTitle(restaurant.getTitle());
            restaurantDTO.setSubtitle(restaurant.getSubtitle());
            restaurantDTO.setFreeShip(restaurant.isFreeship());
            restaurantDTO.setRating(calculateRating(restaurant.getLisRatingRestaurant()));


            restaurantDTOS.add(restaurantDTO);
        }
        return restaurantDTOS;
    }

    private Double calculateRating(Set<RatingRestaurant> listRating){
        if (listRating == null || listRating.isEmpty()) {
            return 0.0;
        }
        double totalPoint = 0;
        for(RatingRestaurant data : listRating){
            totalPoint += data.getRatePoint();
        }

        return totalPoint / listRating.size();
    }

    @Override
    public RestaurantDTO getDetailRestaurant(int id) {
        Optional<Restaurant> restaurant = restaurantReponsitory.findById(id);
        RestaurantDTO restaurantDTO = new RestaurantDTO();
        if(restaurant.isPresent()){
            Restaurant data = restaurant.get();
            List<CategoryDTO> categoryDTOList = new ArrayList<>();
            restaurantDTO.setTitle(data.getTitle());
            restaurantDTO.setSubtitle(data.getSubtitle());
            // Convert image filename to full URL
            if (data.getImage() != null && !data.getImage().isEmpty()) {
                restaurantDTO.setImage("/restaurant/file/" + data.getImage());
            } else {
                restaurantDTO.setImage(data.getImage());
            }
            restaurantDTO.setRating(calculateRating(data.getLisRatingRestaurant()));
            restaurantDTO.setFreeShip(data.isFreeship());
            restaurantDTO.setDescription(data.getDescription());
            restaurantDTO.setOpenDate(data.getOpenDate());
            restaurantDTO.setId(data.getId());

            //listCategory - lấy từ menu_restaurant
            if (data.getLisMenuRestaurant() != null && !data.getLisMenuRestaurant().isEmpty()) {
                // Có dữ liệu trong menu_restaurant - lấy categories từ đó
                for (MenuRestaurant menuRestaurant: data.getLisMenuRestaurant()){
                    if (menuRestaurant.getCategory() == null) continue;
                    
                    CategoryDTO categoryDTO = new CategoryDTO();
                    categoryDTO.setName(menuRestaurant.getCategory().getNameCate());

                    List<MenuDTO>menuDTOList = new ArrayList<>();

                    //menu - lấy tất cả foods của category này
                    if (menuRestaurant.getCategory().getLisFood() != null) {
                        for (Food food : menuRestaurant.getCategory().getLisFood()) {
                            MenuDTO menuDTO = new MenuDTO();
                            menuDTO.setId(food.getId());
                            menuDTO.setTitle(food.getTitle());
                            menuDTO.setDescription(food.getDesc());
                            menuDTO.setPrice(food.getPrice());
                            menuDTO.setTimeShip(food.getTime_ship());
                            
                            // Convert image filename to full URL
                            if (food.getImage() != null && !food.getImage().isEmpty()) {
                                menuDTO.setImage("/menu/file/" + food.getImage());
                            } else {
                                menuDTO.setImage(food.getImage());
                            }
                            
                            menuDTO.setFreeShip(food.isFreeShip());

                            menuDTOList.add(menuDTO);
                        }
                    }
                    categoryDTO.setMenus(menuDTOList);
                    categoryDTOList.add(categoryDTO);
                }
            } else {
                // Nếu không có menu_restaurant, fallback: lấy tất cả categories và foods
                // (Có thể import tất cả categories, nhưng tốt nhất là thêm dữ liệu vào menu_restaurant)
                System.out.println("⚠️ Restaurant " + id + " has no menu_restaurant entries. Please run insert_menu_restaurant.sql");
            }
            restaurantDTO.setCategories(categoryDTOList);
        }
        return restaurantDTO;
    }

    @Override
    public boolean updateRestaurant(int id, MultipartFile file, String title, String subtitle, String description, Boolean is_freeship, String address, String open_date) {
        try {
            Optional<Restaurant> restaurantOptional = restaurantReponsitory.findById(id);
            if (restaurantOptional.isEmpty()) {
                System.err.println("Restaurant not found with id: " + id);
                return false;
            }
            
            Restaurant restaurant = restaurantOptional.get();
            
            // Update file if provided
            if (file != null && !file.isEmpty()) {
                boolean isSaveFileSuccess = fileServiceImp.saveFile(file);
                if (isSaveFileSuccess) {
                    restaurant.setImage(file.getOriginalFilename());
                } else {
                    System.err.println("Failed to save file");
                    return false;
                }
            }
            
            // Update other fields if provided
            if (title != null && !title.trim().isEmpty()) {
                restaurant.setTitle(title.trim());
            }
            
            if (subtitle != null) {
                restaurant.setSubtitle(subtitle.trim());
            }
            
            if (description != null) {
                restaurant.setDescription(description.trim());
            }
            
            if (is_freeship != null) {
                restaurant.setFreeship(is_freeship);
            }
            
            if (address != null && !address.trim().isEmpty()) {
                restaurant.setAddress(address.trim());
            }
            
            if (open_date != null && !open_date.trim().isEmpty()) {
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                    Date openDate = simpleDateFormat.parse(open_date);
                    restaurant.setOpenDate(openDate);
                } catch (Exception e) {
                    System.err.println("Error parsing open_date: " + e.getMessage());
                    // Don't fail if date parsing fails, just skip it
                }
            }
            
            restaurantReponsitory.save(restaurant);
            return true;
        } catch (Exception e) {
            System.err.println("Error updating restaurant: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteRestaurant(int id) {
        try {
            Optional<Restaurant> restaurantOptional = restaurantReponsitory.findById(id);
            if (restaurantOptional.isEmpty()) {
                System.err.println("Restaurant not found with id: " + id);
                return false;
            }
            
            restaurantReponsitory.deleteById(id);
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting restaurant: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
