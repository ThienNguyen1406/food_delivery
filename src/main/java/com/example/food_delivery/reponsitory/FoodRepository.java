package com.example.food_delivery.reponsitory;

import com.example.food_delivery.domain.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodRepository extends JpaRepository<Food, Integer> {
    // Search foods by title
    List<Food> findByTitleContainingIgnoreCase(String keyword);
    
    // Search foods by title or description
    @Query("SELECT f FROM food f WHERE LOWER(f.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(f.desc) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Food> searchFoods(@Param("keyword") String keyword);
}
