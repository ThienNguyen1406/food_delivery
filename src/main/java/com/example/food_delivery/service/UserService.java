package com.example.food_delivery.service;

import com.example.food_delivery.domain.entity.Users;
import com.example.food_delivery.domain.entity.Roles;
import com.example.food_delivery.dto.request.SignupRequest;
import com.example.food_delivery.dto.request.UserUpdateRequest;
import com.example.food_delivery.dto.response.UserDTO;
import com.example.food_delivery.reponsitory.UserReponsitory;
import com.example.food_delivery.reponsitory.RoleRepository;
import com.example.food_delivery.service.imp.UserServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements UserServiceImp {

    @Autowired
    private UserReponsitory userReponsitory;

    @Autowired
    private RoleRepository roleRepository;

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
        try {
            // Get current authenticated user from SecurityContext
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                System.err.println("User not authenticated");
                return null;
            }
            
            // Get username from authentication
            String username = authentication.getName();
            System.out.println("Getting user info for: " + username);
            
            if (username == null || username.isEmpty()) {
                System.err.println("Username is null or empty");
                return null;
            }
            
            // Find user by username
            Users user = userReponsitory.findByUserName(username);
            
            if (user == null) {
                System.err.println("User not found: " + username);
                return null;
            }
            
            System.out.println("User found: " + user.getId() + " - " + user.getUserName());
            return toDTO(user);
        } catch (Exception e) {
            System.err.println("Error getting user info: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public UserDTO updateUser(int userId, UserUpdateRequest request) {
        var user = userReponsitory.findById(userId).orElse(null);
        if (user == null) return null;
        if (request.getFullname() != null) user.setFullName(request.getFullname());
        if (request.getPassword() != null) user.setPassword(passwordEncoder.encode(request.getPassword()));
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        user = userReponsitory.save(user);
        return toDTO(user);
    }

    /**
     * Assign role to user (Admin only)
     */
    public UserDTO assignRoleToUser(int userId, String roleName) {
        var user = userReponsitory.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }
        
        Optional<Roles> roleOpt = roleRepository.findByRoleName(roleName);
        if (roleOpt.isEmpty()) {
            throw new IllegalArgumentException("Role not found: " + roleName);
        }
        
        user.setRoles(roleOpt.get());
        user = userReponsitory.save(user);
        return toDTO(user);
    }

    /**
     * Assign ADMIN role to user (Admin only)
     */
    public UserDTO grantAdminRole(int userId) {
        return assignRoleToUser(userId, "ADMIN");
    }

    /**
     * Assign USER role to user (Admin only)
     */
    public UserDTO grantUserRole(int userId) {
        return assignRoleToUser(userId, "USER");
    }

    /**
     * Delete user by ID (Admin only)
     */
    public boolean deleteUser(int userId) {
        try {
            Optional<Users> userOpt = userReponsitory.findById(userId);
            if (userOpt.isEmpty()) {
                return false;
            }
            
            Users user = userOpt.get();
            // Check if user is admin (prevent deleting admin accounts)
            if (user.getRoles() != null && "ADMIN".equalsIgnoreCase(user.getRoles().getRoleName())) {
                throw new IllegalArgumentException("Không thể xóa tài khoản admin!");
            }
            
            userReponsitory.delete(user);
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Search users by keyword (username or fullname)
     */
    public List<UserDTO> searchUsers(String keyword) {
        try {
            List<Users> users = userReponsitory.searchUsers(keyword);
            return users.stream().map(this::toDTO).collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error searching users: " + e.getMessage());
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    /**
     * Get user by username
     */
    public UserDTO getUserByUsername(String username) {
        try {
            Users user = userReponsitory.findByUserName(username);
            return user == null ? null : toDTO(user);
        } catch (Exception e) {
            System.err.println("Error getting user by username: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private UserDTO toDTO(Users user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUserName(user.getUserName());
        dto.setFullName(user.getFullName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setCreateDate(user.getCreateDate());
        if (user.getRoles() != null) {
            dto.setRoleName(user.getRoles().getRoleName());
        }
        return dto;
    }
}
