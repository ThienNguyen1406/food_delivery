package com.example.food_delivery.entity;

import jakarta.persistence.*;

import java.util.Date;
import java.util.Set;

@Entity (name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "name_cate")
    private String nameCate;

    @Column(name = "created_date")
    private Date createDate;

    @OneToMany(mappedBy = "category")
    private Set<Food> lisFood;

    @OneToMany(mappedBy = "category")
    private Set<MenuRestaurant> lisMenuRestaurant;

    public Set<MenuRestaurant> getLisMenuRestaurant() {
        return lisMenuRestaurant;
    }

    public void setLisMenuRestaurant(Set<MenuRestaurant> lisMenuRestaurant) {
        this.lisMenuRestaurant = lisMenuRestaurant;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameCate() {
        return nameCate;
    }

    public void setNameCate(String nameCate) {
        this.nameCate = nameCate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Set<Food> getLisFood() {
        return lisFood;
    }

    public void setLisFood(Set<Food> lisFood) {
        this.lisFood = lisFood;
    }
}
