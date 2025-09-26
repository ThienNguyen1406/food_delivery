package com.example.food_delivery.entity;

import jakarta.persistence.*;

import java.util.Set;

@Entity(name = "food")
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "title")
    private String title;
    @Column(name = "image")
    private String image;
    @Column(name = "price")
    private double price;

    @ManyToOne
    @JoinColumn(name = "cate_id")
    private Category category;

    @OneToMany(mappedBy = "food")
    private Set<RatingFood> listRatingFood;

    @OneToMany(mappedBy = "food")
    private Set<OrderItem> lisOrderItems;

    public Set<OrderItem> getLisOrderItems() {
        return lisOrderItems;
    }

    public void setLisOrderItems(Set<OrderItem> lisOrderItems) {
        this.lisOrderItems = lisOrderItems;
    }

    public Set<RatingFood> getListRatingFood() {
        return listRatingFood;
    }

    public void setListRatingFood(Set<RatingFood> listRatingFood) {
        this.listRatingFood = listRatingFood;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
