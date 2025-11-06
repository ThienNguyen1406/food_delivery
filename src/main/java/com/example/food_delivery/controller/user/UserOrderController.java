package com.example.food_delivery.controller.user;

import com.example.food_delivery.dto.request.OrderRequest;
import com.example.food_delivery.dto.response.OrderDTO;
import com.example.food_delivery.dto.response.ResponseData;
import com.example.food_delivery.service.imp.OrderServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController("userOrderController")
@RequestMapping("/order")
public class UserOrderController {

    @Autowired
    OrderServiceImp orderServiceImp;
    
    // Note: orderServiceImp is the interface, but we need to cast to OrderService to call checkoutFromCart
    // Or we can add checkoutFromCart to OrderServiceImp interface (which we did)

    /**
     * POST /order - Tạo đơn hàng mới
     * Yêu cầu authentication (user)
     */
    @PostMapping()
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest) {
        ResponseData responseData = new ResponseData();
        
        try {
            // Validate input
            if (orderRequest.getUserId() <= 0) {
                responseData.setStatus(400);
                responseData.setSuccess(false);
                responseData.setData(false);
                responseData.setDesc("User ID không hợp lệ!");
                return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
            }
            
            if (orderRequest.getResId() <= 0) {
                responseData.setStatus(400);
                responseData.setSuccess(false);
                responseData.setData(false);
                responseData.setDesc("Restaurant ID không hợp lệ!");
                return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
            }
            
            if (orderRequest.getFoodIds() == null || orderRequest.getFoodIds().length == 0) {
                responseData.setStatus(400);
                responseData.setSuccess(false);
                responseData.setData(false);
                responseData.setDesc("Danh sách món ăn không được để trống!");
                return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
            }
            
            // Create order
            boolean result = orderServiceImp.insertOrder(orderRequest);
            
            if (result) {
                responseData.setStatus(200);
                responseData.setSuccess(true);
                responseData.setData(true);
                responseData.setDesc("Tạo đơn hàng thành công!");
            } else {
                responseData.setStatus(400);
                responseData.setSuccess(false);
                responseData.setData(false);
                responseData.setDesc("Tạo đơn hàng thất bại!");
            }
            
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error creating order: " + e.getMessage());
            e.printStackTrace();
            responseData.setStatus(500);
            responseData.setSuccess(false);
            responseData.setData(false);
            responseData.setDesc("Lỗi server khi tạo đơn hàng: " + e.getMessage());
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * GET /order - Lấy danh sách đơn hàng của user hiện tại
     * Yêu cầu authentication (user)
     * Query params: userId (optional) - nếu không có, cần lấy từ token
     */
    @GetMapping()
    public ResponseEntity<?> getOrders(@RequestParam(required = false) Integer userId) {
        ResponseData responseData = new ResponseData();
        try {
            List<OrderDTO> orders;
            
            if (userId != null && userId > 0) {
                // Lấy orders theo userId as DTOs (to avoid circular reference)
                orders = orderServiceImp.getOrdersByUserIdAsDTO(userId);
            } else {
                // Lấy tất cả orders (tạm thời, sau này sẽ lấy từ token)
                // For now, return empty list or get from token
                orders = new ArrayList<>();
            }
            
            responseData.setStatus(200);
            responseData.setSuccess(true);
            responseData.setData(orders);
            responseData.setDesc("Lấy danh sách đơn hàng thành công!");
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error getting orders: " + e.getMessage());
            e.printStackTrace();
            responseData.setStatus(500);
            responseData.setSuccess(false);
            responseData.setData(null);
            responseData.setDesc("Lỗi khi lấy danh sách đơn hàng: " + e.getMessage());
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * GET /order/{id} - Lấy đơn hàng theo ID
     * Yêu cầu authentication (user)
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable int id) {
        ResponseData responseData = new ResponseData();
        try {
            // Note: Cần thêm method getOrderById vào OrderService
            responseData.setStatus(501);
            responseData.setSuccess(false);
            responseData.setData(null);
            responseData.setDesc("Chức năng lấy đơn hàng theo ID chưa được triển khai");
            return new ResponseEntity<>(responseData, HttpStatus.NOT_IMPLEMENTED);
        } catch (Exception e) {
            responseData.setStatus(500);
            responseData.setSuccess(false);
            responseData.setData(null);
            responseData.setDesc("Lỗi khi lấy đơn hàng: " + e.getMessage());
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * GET /order/user/{userId} - Lấy đơn hàng theo user ID
     * Yêu cầu authentication (user hoặc admin)
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getOrdersByUserId(@PathVariable int userId) {
        ResponseData responseData = new ResponseData();
        try {
            // Validate userId
            if (userId <= 0) {
                responseData.setStatus(400);
                responseData.setSuccess(false);
                responseData.setData(null);
                responseData.setDesc("User ID không hợp lệ!");
                return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
            }
            
            List<OrderDTO> orders = orderServiceImp.getOrdersByUserIdAsDTO(userId);
            
            responseData.setStatus(200);
            responseData.setSuccess(true);
            responseData.setData(orders);
            responseData.setDesc("Lấy danh sách đơn hàng thành công!");
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error getting orders by user id: " + e.getMessage());
            e.printStackTrace();
            responseData.setStatus(500);
            responseData.setSuccess(false);
            responseData.setData(null);
            responseData.setDesc("Lỗi khi lấy đơn hàng: " + e.getMessage());
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * POST /order/checkout - Checkout from cart (tạo order từ cart và clear cart)
     * Yêu cầu authentication (user)
     * Body: { userId: int }
     */
    @PostMapping("/checkout")
    public ResponseEntity<?> checkoutFromCart(@RequestBody CheckoutRequest request) {
        ResponseData responseData = new ResponseData();
        
        try {
            // Validate input
            if (request.getUserId() <= 0) {
                responseData.setStatus(400);
                responseData.setSuccess(false);
                responseData.setData(false);
                responseData.setDesc("User ID không hợp lệ!");
                return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
            }
            
            // Checkout from cart
            boolean result = orderServiceImp.checkoutFromCart(request.getUserId());
            
            if (result) {
                responseData.setStatus(200);
                responseData.setSuccess(true);
                responseData.setData(true);
                responseData.setDesc("Đặt hàng thành công!");
            } else {
                responseData.setStatus(400);
                responseData.setSuccess(false);
                responseData.setData(false);
                responseData.setDesc("Đặt hàng thất bại! Vui lòng kiểm tra giỏ hàng của bạn.");
            }
            
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error in checkout from cart: " + e.getMessage());
            e.printStackTrace();
            responseData.setStatus(500);
            responseData.setSuccess(false);
            responseData.setData(false);
            responseData.setDesc("Lỗi server khi đặt hàng: " + e.getMessage());
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // Inner class for checkout request
    public static class CheckoutRequest {
        private int userId;
        
        public int getUserId() {
            return userId;
        }
        
        public void setUserId(int userId) {
            this.userId = userId;
        }
    }
}

