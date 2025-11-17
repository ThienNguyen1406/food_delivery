package com.example.food_delivery.controller.admin;

import com.example.food_delivery.core.response.ApiResponse;
import com.example.food_delivery.dto.request.SignupRequest;
import com.example.food_delivery.dto.request.UserUpdateRequest;
import com.example.food_delivery.dto.response.UserDTO;
import com.example.food_delivery.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController("adminUserController")
@RequestMapping("/user")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminUserController {

    UserService userService;

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUser() {
        ApiResponse response = new ApiResponse();
        response.setCode(HttpStatus.OK.value());
        response.setResult(userService.getAllUser());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * GET /user/search?keyword=... - Tìm kiếm users
     * Yêu cầu quyền ADMIN
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> searchUsers(@RequestParam String keyword) {
        ApiResponse response = new ApiResponse();
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                response.setCode(HttpStatus.BAD_REQUEST.value());
                response.setMessage("Keyword không được để trống!");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            
            var users = userService.searchUsers(keyword.trim());
            response.setCode(HttpStatus.OK.value());
            response.setResult(users);
            response.setMessage("Tìm kiếm users thành công!");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error searching users: " + e.getMessage());
            e.printStackTrace();
            response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Lỗi server khi tìm kiếm users: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * POST /user - Tạo user mới (Admin only)
     */
    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@RequestBody SignupRequest signupRequest) {
        ApiResponse response = new ApiResponse();
        try {
            // Validate input
            if (signupRequest.getUserName() == null || signupRequest.getUserName().trim().isEmpty()) {
                response.setCode(HttpStatus.BAD_REQUEST.value());
                response.setMessage("Username không được để trống!");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            
            if (signupRequest.getPassword() == null || signupRequest.getPassword().trim().isEmpty()) {
                response.setCode(HttpStatus.BAD_REQUEST.value());
                response.setMessage("Password không được để trống!");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            
            // Check if username already exists - try to get user by username
            // If getUserByUsername returns non-null, username already exists
            try {
                var existingUser = userService.getUserByUsername(signupRequest.getUserName().trim());
                if (existingUser != null) {
                    response.setCode(HttpStatus.BAD_REQUEST.value());
                    response.setMessage("Username đã tồn tại!");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }
            } catch (Exception e) {
                // If getUserByUsername throws exception, username might not exist - continue
            }
            
            UserDTO user = userService.addUser(signupRequest);
            if (user != null) {
                response.setCode(HttpStatus.OK.value());
                response.setResult(user);
                response.setMessage("Tạo user thành công!");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                response.setMessage("Tạo user thất bại!");
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (IllegalArgumentException e) {
            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.err.println("Error creating user: " + e.getMessage());
            e.printStackTrace();
            response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Lỗi server khi tạo user: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable int id) {
        ApiResponse response = new ApiResponse();
        UserDTO user = userService.getUser(id);
        if (user != null) {
            response.setCode(HttpStatus.OK.value());
            response.setResult(user);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.setCode(HttpStatus.NOT_FOUND.value());
            response.setMessage("User not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyInfo() {
        ApiResponse response = new ApiResponse();
        UserDTO user = userService.getMyInfo();
        if (user != null) {
            response.setCode(HttpStatus.OK.value());
            response.setResult(user);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.setCode(HttpStatus.UNAUTHORIZED.value());
            response.setMessage("User not authenticated");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable int id, @RequestBody UserUpdateRequest request) {
        ApiResponse response = new ApiResponse();
        UserDTO user = userService.updateUser(id, request);
        if (user != null) {
            response.setCode(HttpStatus.OK.value());
            response.setResult(user);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.setCode(HttpStatus.NOT_FOUND.value());
            response.setMessage("User not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        ApiResponse response = new ApiResponse();
        try {
            // Validate id
            if (id <= 0) {
                response.setCode(HttpStatus.BAD_REQUEST.value());
                response.setMessage("User ID không hợp lệ!");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            
            UserDTO user = userService.getUser(id);
            if (user == null) {
                response.setCode(HttpStatus.NOT_FOUND.value());
                response.setMessage("User not found");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            
            // Delete user
            boolean deleted = userService.deleteUser(id);
            if (deleted) {
                response.setCode(HttpStatus.OK.value());
                response.setResult(true);
                response.setMessage("Xóa user thành công!");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                response.setMessage("Xóa user thất bại!");
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (IllegalArgumentException e) {
            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.err.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
            response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Lỗi server khi xóa user: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Grant ADMIN role to user (Admin only)
     */
    @PutMapping("/{id}/grant-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> grantAdminRole(@PathVariable int id) {
        ApiResponse response = new ApiResponse();
        try {
            UserDTO user = userService.grantAdminRole(id);
            if (user != null) {
                response.setCode(HttpStatus.OK.value());
                response.setResult(user);
                response.setMessage("Admin role granted successfully");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setCode(HttpStatus.NOT_FOUND.value());
                response.setMessage("User not found");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Grant USER role to user (Admin only)
     */
    @PutMapping("/{id}/grant-user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> grantUserRole(@PathVariable int id) {
        ApiResponse response = new ApiResponse();
        try {
            UserDTO user = userService.grantUserRole(id);
            if (user != null) {
                response.setCode(HttpStatus.OK.value());
                response.setResult(user);
                response.setMessage("User role granted successfully");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setCode(HttpStatus.NOT_FOUND.value());
                response.setMessage("User not found");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Check if current user is admin (Admin only)
     */
    @GetMapping("/check-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> checkAdmin() {
        ApiResponse response = new ApiResponse();
        // If it reaches here, user is admin
        response.setCode(HttpStatus.OK.value());
        response.setResult(true);
        response.setMessage("User has ADMIN role");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * PUT /user/profile - Cập nhật thông tin profile của user hiện tại
     * Yêu cầu authentication (user tự cập nhật profile của mình)
     * Không cần quyền ADMIN - user thường có thể sử dụng endpoint này
     * Body: { fullname: String, password: String (optional) }
     */
    @PutMapping("/profile")
    // Không có @PreAuthorize("hasRole('ADMIN')") - user thường có thể sử dụng
    public ResponseEntity<?> updateMyProfile(@RequestBody UserProfileUpdateRequest request) {
        ApiResponse response = new ApiResponse();
        
        try {
            // Get current user info
            UserDTO currentUser = userService.getMyInfo();
            if (currentUser == null) {
                response.setCode(HttpStatus.UNAUTHORIZED.value());
                response.setMessage("User not authenticated");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
            
            // Create UserUpdateRequest with only allowed fields
            UserUpdateRequest updateRequest = new UserUpdateRequest();
            if (request.getFullname() != null && !request.getFullname().trim().isEmpty()) {
                updateRequest.setFullname(request.getFullname().trim());
            }
            if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
                // Only update password if provided
                updateRequest.setPassword(request.getPassword());
            }
            if (request.getPhoneNumber() != null && !request.getPhoneNumber().trim().isEmpty()) {
                updateRequest.setPhoneNumber(request.getPhoneNumber().trim());
            }
            // Note: userName is not updateable via this endpoint
            // userName is the login identifier and should not be changed
            
            // Update user profile
            UserDTO updatedUser = userService.updateUser(currentUser.getId(), updateRequest);
            
            if (updatedUser != null) {
                response.setCode(HttpStatus.OK.value());
                response.setResult(updatedUser);
                response.setMessage("Cập nhật thông tin thành công!");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.setCode(HttpStatus.NOT_FOUND.value());
                response.setMessage("User not found");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            System.err.println("Error updating user profile: " + e.getMessage());
            e.printStackTrace();
            response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Lỗi server khi cập nhật thông tin: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Request DTO for user profile update
     */
    public static class UserProfileUpdateRequest {
        private String fullname;
        private String password;
        private String phoneNumber;

        public String getFullname() {
            return fullname;
        }

        public void setFullname(String fullname) {
            this.fullname = fullname;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
    }
}

