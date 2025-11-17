package com.example.food_delivery.reponsitory;

import com.example.food_delivery.domain.entity.Promo;
import com.example.food_delivery.domain.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PromoRepository extends JpaRepository<Promo, Integer> {
    /**
     * Tìm promo theo restaurant
     */
    List<Promo> findByRestaurant(Restaurant restaurant);
    
    /**
     * Tìm promo theo restaurant ID
     */
    List<Promo> findByRestaurantId(Integer restaurantId);
    
    /**
     * Tìm promo đang active (trong khoảng thời gian startDate và endDate)
     */
    @Query("SELECT p FROM promo p WHERE p.restaurant.id = :restaurantId " +
           "AND p.startDate <= :currentDate AND p.endDate >= :currentDate")
    List<Promo> findActivePromosByRestaurant(@Param("restaurantId") Integer restaurantId, 
                                              @Param("currentDate") Date currentDate);
    
    /**
     * Tìm tất cả promo đang active
     */
    @Query("SELECT p FROM promo p WHERE p.startDate <= :currentDate AND p.endDate >= :currentDate")
    List<Promo> findAllActivePromos(@Param("currentDate") Date currentDate);
}

