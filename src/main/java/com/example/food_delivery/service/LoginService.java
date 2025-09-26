package com.example.food_delivery.service;

import com.example.food_delivery.dto.UserDTO;
import com.example.food_delivery.entity.Roles;
import com.example.food_delivery.entity.Users;
import com.example.food_delivery.payload.request.SignUpRequest;
import com.example.food_delivery.reponsitory.UserReponsitory;
import com.example.food_delivery.service.imp.LoginServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Service
public class LoginService implements LoginServiceImp {
    @Autowired
    UserReponsitory userReponsitory;

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
        List<Users> listUser =userReponsitory.findByUserNameAndPassword(username, password);
        return listUser.size() > 0;

    }

    @Override
    public Boolean addUser(SignUpRequest signUpRequest) {
        Roles roles = new Roles();
        roles.setId(signUpRequest.getRoleId());
        Users users = new Users();
        users.setFullName(signUpRequest.getFullName());
        users.setPassword(signUpRequest.getPassword());
        users.setUserName(signUpRequest.getEmail());
        users.setRoles(roles);

        //save thành công id sẽ có giá trị
        try{
            userReponsitory.save(users);
            return true;
        }catch(Exception e){
            return false;
        }
    }
}
