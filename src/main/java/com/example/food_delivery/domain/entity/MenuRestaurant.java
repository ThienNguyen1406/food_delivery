package com.example.food_delivery.domain.entity;

import com.example.food_delivery.domain.entity.keys.KeyMenuRestaurant;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "menu_restaurant")
public class MenuRestaurant {
    @EmbeddedId
    KeyMenuRestaurant keys;

    @ManyToOne
    @MapsId("cateId")
    @JoinColumn(name = "cate_id")
    Category category;

    @ManyToOne
    @MapsId("resId")
    @JoinColumn(name = "res_id")
    Restaurant restaurant;

    @Column(name = "create_date")
    Date createDate;

    @Column(name = "update_date")
    Date updateDate;

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public KeyMenuRestaurant getKeys() {
        return keys;
    }

    public void setKeys(KeyMenuRestaurant keys) {
        this.keys = keys;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
