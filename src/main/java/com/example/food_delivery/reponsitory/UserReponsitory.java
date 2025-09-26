package com.example.food_delivery.reponsitory;

import com.example.food_delivery.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserReponsitory extends JpaRepository <Users,Integer>{
    //custom query login
    /**
     *
     * @param userName
     * @param password
     * @return
     */
    List<Users> findByUserNameAndPassword(String userName, String password);
}
