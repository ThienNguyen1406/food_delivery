package com.example.food_delivery.service;

import com.example.food_delivery.domain.entity.Promo;
import com.example.food_delivery.domain.entity.Restaurant;
import com.example.food_delivery.dto.response.PromoDTO;
import com.example.food_delivery.reponsitory.PromoRepository;
import com.example.food_delivery.reponsitory.RestaurantReponsitory;
import com.example.food_delivery.service.imp.PromoServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PromoService implements PromoServiceImp {
    
    @Autowired
    PromoRepository promoRepository;
    
    @Autowired
    RestaurantReponsitory restaurantRepository;
    
    @Override
    public List<PromoDTO> getAllActivePromos() {
        Date currentDate = new Date();
        List<Promo> promos = promoRepository.findAllActivePromos(currentDate);
        return convertToDTOList(promos);
    }
    
    @Override
    public List<PromoDTO> getActivePromosByRestaurant(Integer restaurantId) {
        Date currentDate = new Date();
        List<Promo> promos = promoRepository.findActivePromosByRestaurant(restaurantId, currentDate);
        return convertToDTOList(promos);
    }
    
    @Override
    public PromoDTO validatePromo(Integer restaurantId, Integer promoId) {
        if (promoId == null || promoId <= 0) {
            return null;
        }
        
        Optional<Promo> promoOpt = promoRepository.findById(promoId);
        if (!promoOpt.isPresent()) {
            return null;
        }
        
        Promo promo = promoOpt.get();
        Date currentDate = new Date();
        
        // Check if promo is active (within date range)
        if (promo.getStartDate().after(currentDate) || promo.getEndDate().before(currentDate)) {
            return null;
        }
        
        // Check if promo belongs to restaurant (if restaurantId is provided)
        if (restaurantId != null && restaurantId > 0) {
            if (promo.getRestaurant() == null || promo.getRestaurant().getId() != restaurantId) {
                return null;
            }
        }
        
        return convertToDTO(promo);
    }
    
    @Override
    public long calculateDiscount(long totalPrice, Promo promo) {
        if (promo == null || totalPrice <= 0) {
            return 0;
        }
        
        int percent = promo.getPercent();
        if (percent <= 0 || percent > 100) {
            return 0;
        }
        
        // Calculate discount: totalPrice * percent / 100
        return (totalPrice * percent) / 100;
    }
    
    /**
     * Convert Promo entity to PromoDTO
     */
    private PromoDTO convertToDTO(Promo promo) {
        if (promo == null) {
            return null;
        }
        
        PromoDTO dto = new PromoDTO();
        dto.setId(promo.getId());
        dto.setPercent(promo.getPercent());
        dto.setStartDate(promo.getStartDate());
        dto.setEndDate(promo.getEndDate());
        
        if (promo.getRestaurant() != null) {
            dto.setRestaurantId(promo.getRestaurant().getId());
            dto.setRestaurantName(promo.getRestaurant().getTitle());
        }
        
        return dto;
    }
    
    /**
     * Convert list of Promo entities to list of PromoDTO
     */
    private List<PromoDTO> convertToDTOList(List<Promo> promos) {
        List<PromoDTO> dtoList = new ArrayList<>();
        for (Promo promo : promos) {
            PromoDTO dto = convertToDTO(promo);
            if (dto != null) {
                dtoList.add(dto);
            }
        }
        return dtoList;
    }
}

