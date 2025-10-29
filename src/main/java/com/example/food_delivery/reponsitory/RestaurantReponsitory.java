package com.example.food_delivery.reponsitory;

import com.example.food_delivery.domain.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantReponsitory extends JpaRepository<Restaurant, Integer> {

}
