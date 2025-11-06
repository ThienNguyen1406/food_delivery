package com.example.food_delivery.reponsitory;

import com.example.food_delivery.domain.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserReponsitory extends JpaRepository <Users,Integer>{
    //custom query login
    List<Users> findByUserNameAndPassword(String userName, String password);
    Users findByUserName(String userName);
    Boolean existsByUserName(String username);
    
    // Search users by username or fullname
    @Query("SELECT u FROM users u WHERE LOWER(u.userName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Users> searchUsers(@Param("keyword") String keyword);
}
