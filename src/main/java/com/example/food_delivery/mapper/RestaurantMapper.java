package com.example.food_delivery.mapper;


import com.example.food_delivery.domain.entity.Restaurant;
import com.example.food_delivery.dto.response.RestaurantDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
        componentModel = "spring",
        uses = {CategoryMapper.class, FoodMapper.class})
public interface RestaurantMapper {

    @Mappings({
        @Mapping(source = "subtitle", target = "subtitle"),
        @Mapping(source = "openDate", target = "openDate"),
    })
    RestaurantDTO restaurantToRestaurantDTO(Restaurant restaurant);
}
