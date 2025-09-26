package com.example.food_delivery.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Date;
import java.util.Set;

@Entity(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "user_name")
    private String userName;

    @Column(name = "password")
    private String password;

    @Column(name = "fullname")
    private String fullName;

    @Column(name =  "created_date")
    private Date createDate;



    //map entity
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Roles roles;


    @OneToMany(mappedBy = "users")
    private Set<RatingFood> listRatingFood;


    @OneToMany(mappedBy = "users")
    private Set<RatingRestaurant> listRatingRestaurant;


    @OneToMany(mappedBy = "users")
    private Set<Orders> listOrders;

    public Set<Orders> getListOrders() {
        return listOrders;
    }

    public void setListOrders(Set<Orders> listOrders) {
        this.listOrders = listOrders;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public Set<RatingRestaurant> getListRatingRestaurant() {
        return listRatingRestaurant;
    }

    public void setListRatingRestaurant(Set<RatingRestaurant> listRatingRestaurant) {
        this.listRatingRestaurant = listRatingRestaurant;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Roles getRoles() {
        return roles;
    }

    public void setRoles(Roles roles) {
        this.roles = roles;
    }
}
