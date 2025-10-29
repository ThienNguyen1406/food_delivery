package com.example.food_delivery.service;

import com.example.food_delivery.domain.entity.Users;
import com.example.food_delivery.dto.request.SignupRequest;
import com.example.food_delivery.dto.request.UserUpdateRequest;
import com.example.food_delivery.dto.response.UserDTO;
import com.example.food_delivery.reponsitory.UserReponsitory;
import com.example.food_delivery.service.imp.UserServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserServiceImp {

    @Autowired
    private UserReponsitory userReponsitory;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<UserDTO> getAllUser() {
        return userReponsitory.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public UserDTO addUser(SignupRequest signupRequest) {
        Users user = new Users();
        user.setFullName(signupRequest.getFullname());
        user.setUserName(signupRequest.getUserName());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user = userReponsitory.save(user);
        return toDTO(user);
    }

    @Override
    public UserDTO getUser(int id) {
        var user = userReponsitory.findById(id).orElse(null);
        return user == null ? null : toDTO(user);
    }

    @Override
    public UserDTO getMyInfo() {
        return null;
    }

    @Override
    public UserDTO updateUser(int userId, UserUpdateRequest request) {
        var user = userReponsitory.findById(userId).orElse(null);
        if (user == null) return null;
        if (request.getFullname() != null) user.setFullName(request.getFullname());
        if (request.getPassword() != null) user.setPassword(passwordEncoder.encode(request.getPassword()));
        user = userReponsitory.save(user);
        return toDTO(user);
    }

    private UserDTO toDTO(Users user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUserName(user.getUserName());
        dto.setFullName(user.getFullName());
        dto.setCreateDate(user.getCreateDate());
        return dto;
    }
}
