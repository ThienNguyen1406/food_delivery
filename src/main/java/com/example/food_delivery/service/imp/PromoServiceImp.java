package com.example.food_delivery.service.imp;

import com.example.food_delivery.domain.entity.Promo;
import com.example.food_delivery.dto.response.PromoDTO;

import java.util.List;

public interface PromoServiceImp {
    /**
     * Lấy tất cả promo codes đang active
     */
    List<PromoDTO> getAllActivePromos();
    
    /**
     * Lấy promo codes theo restaurant ID đang active
     */
    List<PromoDTO> getActivePromosByRestaurant(Integer restaurantId);
    
    /**
     * Validate và lấy thông tin promo code
     * @param restaurantId - Restaurant ID
     * @param promoId - Promo ID
     * @return PromoDTO nếu hợp lệ, null nếu không hợp lệ
     */
    PromoDTO validatePromo(Integer restaurantId, Integer promoId);
    
    /**
     * Tính discount amount từ promo
     * @param totalPrice - Tổng tiền
     * @param promo - Promo object
     * @return Số tiền được giảm
     */
    long calculateDiscount(long totalPrice, Promo promo);
}

