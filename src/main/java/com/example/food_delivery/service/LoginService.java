package com.example.food_delivery.service;

import com.example.food_delivery.dto.request.SignupRequest;
import com.example.food_delivery.dto.response.UserDTO;
import com.example.food_delivery.domain.entity.Roles;
import com.example.food_delivery.domain.entity.Users;
import com.example.food_delivery.reponsitory.UserReponsitory;
import com.example.food_delivery.service.imp.LoginServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LoginService implements LoginServiceImp {
    @Autowired
    UserReponsitory userReponsitory;
    @Autowired
    PasswordEncoder passwordEncoder;

    public List<UserDTO> getAllUsers() {
        List<Users> listUser = userReponsitory.findAll();
        List<UserDTO> userDTOList = new ArrayList<>();

        for (Users user : listUser) {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setUserName(user.getUserName());
            userDTO.setPassword(user.getPassword());
            userDTO.setFullName(user.getFullName());
            userDTO.setCreateDate(user.getCreateDate());

            userDTOList.add(userDTO);

        }
        return userDTOList;
    }

    @Override
    public Boolean checkLogin(String username, String password) {
       Users users =userReponsitory.findByUserName(username);
        return passwordEncoder.matches(password,users.getPassword());
    }

    @Override
    public Boolean addUser(SignupRequest signUpRequest) {
        Users users = new Users();
        users.setFullName(signUpRequest.getFullname());
        users.setPassword(signUpRequest.getPassword());
        users.setUserName(signUpRequest.getUserName());

        //save thành công id sẽ có giá trị
        try{
            userReponsitory.save(users);
            return true;
        }catch(Exception e){
            return false;
        }
    }




}
