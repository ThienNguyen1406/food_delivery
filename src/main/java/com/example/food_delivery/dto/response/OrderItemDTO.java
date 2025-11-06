package com.example.food_delivery.dto.response;

import java.util.Date;

public class OrderItemDTO {
    private int orderId;
    private int foodId;
    private String foodTitle;
    private String foodImage;
    private double foodPrice;
    private Date createDate;
    
    public OrderItemDTO() {
    }
    
    public int getOrderId() {
        return orderId;
    }
    
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
    
    public int getFoodId() {
        return foodId;
    }
    
    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }
    
    public String getFoodTitle() {
        return foodTitle;
    }
    
    public void setFoodTitle(String foodTitle) {
        this.foodTitle = foodTitle;
    }
    
    public String getFoodImage() {
        return foodImage;
    }
    
    public void setFoodImage(String foodImage) {
        this.foodImage = foodImage;
    }
    
    public double getFoodPrice() {
        return foodPrice;
    }
    
    public void setFoodPrice(double foodPrice) {
        this.foodPrice = foodPrice;
    }
    
    public Date getCreateDate() {
        return createDate;
    }
    
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}

