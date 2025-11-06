package com.example.food_delivery.service;

import com.example.food_delivery.domain.entity.*;
import com.example.food_delivery.domain.entity.keys.KeyOrderItem;
import com.example.food_delivery.dto.request.OrderRequest;
import com.example.food_delivery.dto.response.CartDTO;
import com.example.food_delivery.dto.response.CartItemDTO;
import com.example.food_delivery.dto.response.OrderDTO;
import com.example.food_delivery.dto.response.OrderItemDTO;
import com.example.food_delivery.reponsitory.*;
import com.example.food_delivery.service.imp.CartServiceImp;
import com.example.food_delivery.service.imp.OrderServiceImp;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService implements OrderServiceImp {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemRepository orderItemRepository;
    
    @Autowired
    CartServiceImp cartService;
    
    @Autowired
    FoodRepository foodRepository;
    
    @Autowired
    CategoryRepository categoryRepository;

    @Transactional
    @Override
    public boolean insertOrder(OrderRequest orderRequest) {

        try {

            Restaurant restaurant = new Restaurant();
            restaurant.setId(orderRequest.getResId());

            Users users = new Users();
            users.setId(orderRequest.getUserId());

            Orders orders = new Orders();
            orders.setUsers(users);
            orders.setRestaurant(restaurant);
            orders.setCreateDate(new Date());
            orderRepository.save(orders);

            List<OrderItem> orderItems = new ArrayList<>();
            for (int foodId : orderRequest.getFoodIds()) {

                Food food = new Food();
                food.setId(foodId);

                OrderItem orderItem = new OrderItem();
                KeyOrderItem keyOrderItem = new KeyOrderItem(orders.getId(), foodId);
                orderItem.setKeyOrderItem(keyOrderItem);
                orderItem.setCreateDate(new Date());
                orderItems.add(orderItem);
            }
            orderItemRepository.saveAll(orderItems);

            return true;
        } catch (Exception e) {
            System.out.println("Error insert order" + e);
            return false;
        }
    }

    @Override
    public List<Orders> getAllOrders() {
        try {
            return orderRepository.findAll();
        } catch (Exception e) {
            System.err.println("Error getting all orders: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public Optional<Orders> getOrderById(int id) {
        try {
            return orderRepository.findById(id);
        } catch (Exception e) {
            System.err.println("Error getting order by id: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<Orders> getOrdersByUserId(int userId) {
        try {
            // Note: Cần thêm method findByUserId vào OrderRepository nếu muốn filter theo user
            // Hiện tại trả về tất cả orders và filter ở service
            List<Orders> allOrders = orderRepository.findAll();
            return allOrders.stream()
                    .filter(order -> order.getUsers() != null && order.getUsers().getId() == userId)
                    .toList();
        } catch (Exception e) {
            System.err.println("Error getting orders by user id: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<OrderDTO> getOrdersByUserIdAsDTO(int userId) {
        try {
            System.out.println("=== getOrdersByUserIdAsDTO() called ===");
            System.out.println("UserId: " + userId);
            
            // Get orders as entities
            List<Orders> orders = getOrdersByUserId(userId);
            System.out.println("Found " + orders.size() + " orders for user " + userId);
            
            // Convert to DTOs
            List<OrderDTO> orderDTOs = new ArrayList<>();
            for (Orders order : orders) {
                OrderDTO orderDTO = new OrderDTO();
                orderDTO.setId(order.getId());
                orderDTO.setCreateDate(order.getCreateDate());
                
                // User info
                if (order.getUsers() != null) {
                    orderDTO.setUserId(order.getUsers().getId());
                    orderDTO.setUserName(order.getUsers().getUserName());
                }
                
                // Restaurant info
                if (order.getRestaurant() != null) {
                    orderDTO.setRestaurantId(order.getRestaurant().getId());
                    orderDTO.setRestaurantTitle(order.getRestaurant().getTitle());
                }
                
                // Order items - fetch from OrderItemRepository
                List<OrderItemDTO> itemDTOs = new ArrayList<>();
                try {
                    // Get order items by order ID using key
                    List<OrderItem> orderItems = orderItemRepository.findAll().stream()
                            .filter(item -> item.getKeyOrderItem() != null && 
                                    item.getKeyOrderItem().getOrderId() == order.getId())
                            .toList();
                    
                    System.out.println("Order " + order.getId() + " has " + orderItems.size() + " items");
                    
                    for (OrderItem orderItem : orderItems) {
                        OrderItemDTO itemDTO = new OrderItemDTO();
                        itemDTO.setOrderId(order.getId());
                        itemDTO.setCreateDate(orderItem.getCreateDate());
                        
                        if (orderItem.getFood() != null) {
                            Food food = orderItem.getFood();
                            itemDTO.setFoodId(food.getId());
                            itemDTO.setFoodTitle(food.getTitle());
                            itemDTO.setFoodPrice(food.getPrice());
                            
                            // Convert image filename to full URL
                            if (food.getImage() != null && !food.getImage().isEmpty()) {
                                itemDTO.setFoodImage("/menu/file/" + food.getImage());
                            } else {
                                itemDTO.setFoodImage(food.getImage());
                            }
                        }
                        
                        itemDTOs.add(itemDTO);
                    }
                } catch (Exception e) {
                    System.err.println("Error fetching order items for order " + order.getId() + ": " + e.getMessage());
                    e.printStackTrace();
                }
                orderDTO.setItems(itemDTOs);
                
                orderDTOs.add(orderDTO);
            }
            
            System.out.println("Converted to " + orderDTOs.size() + " OrderDTOs");
            return orderDTOs;
        } catch (Exception e) {
            System.err.println("Error getting orders by user id as DTO: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public boolean updateOrder(int id, String status) {
        try {
            Optional<Orders> orderOptional = orderRepository.findById(id);
            if (orderOptional.isPresent()) {
                Orders order = orderOptional.get();
                if (order != null) {
                    // Note: Orders entity chưa có status field, cần thêm vào entity nếu muốn lưu status
                    // Hiện tại chỉ update createDate
                    orderRepository.save(order);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error updating order: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteOrder(int id) {
        try {
            Optional<Orders> orderOptional = orderRepository.findById(id);
            if (orderOptional.isPresent()) {
                // Xóa order items trước
                Orders order = orderOptional.get();
                if (order != null && order.getListOrderItems() != null && !order.getListOrderItems().isEmpty()) {
                    List<OrderItem> orderItems = new ArrayList<>(order.getListOrderItems());
                    orderItemRepository.deleteAll(orderItems);
                }
                // Sau đó xóa order
                if (order != null) {
                    orderRepository.delete(order);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error deleting order: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Checkout from cart - Tạo order từ cart và clear cart
     * @param userId - User ID
     * @return true nếu thành công, false nếu thất bại
     */
    @Override
    @Transactional
    public boolean checkoutFromCart(int userId) {
        try {
            System.out.println("=== checkoutFromCart() called ===");
            System.out.println("UserId: " + userId);
            
            // Validate userId
            if (userId <= 0) {
                System.err.println("❌ Invalid userId: " + userId);
                return false;
            }
            
            // Get cart from userId
            CartDTO cart = cartService.getCartByUserId(userId);
            if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
                System.err.println("❌ Cart is empty or null for user: " + userId);
                return false;
            }
            
            System.out.println("✅ Cart found with " + cart.getItems().size() + " items");
            
            // Get restaurant ID from first cart item
            // Food → Category → MenuRestaurant → Restaurant
            int restaurantId = 1; // Default restaurant ID
            if (!cart.getItems().isEmpty()) {
                CartItemDTO firstItem = cart.getItems().get(0);
                int foodId = firstItem.getFoodId();
                
                // Find restaurant ID from food
                Optional<Food> foodOpt = foodRepository.findById(foodId);
                if (foodOpt.isPresent()) {
                    Food food = foodOpt.get();
                    if (food.getCategory() != null) {
                        Category category = food.getCategory();
                        // Find restaurant from category's menu_restaurant
                        if (category.getLisMenuRestaurant() != null && !category.getLisMenuRestaurant().isEmpty()) {
                            MenuRestaurant menuRestaurant = category.getLisMenuRestaurant().iterator().next();
                            if (menuRestaurant != null && menuRestaurant.getRestaurant() != null) {
                                restaurantId = menuRestaurant.getRestaurant().getId();
                                System.out.println("✅ Restaurant ID found: " + restaurantId);
                            }
                        }
                    }
                }
            }
            
            // Build foodIds array with quantity
            List<Integer> foodIdsList = new ArrayList<>();
            for (CartItemDTO item : cart.getItems()) {
                for (int i = 0; i < item.getQuantity(); i++) {
                    foodIdsList.add(item.getFoodId());
                }
            }
            int[] foodIds = foodIdsList.stream().mapToInt(i -> i).toArray();
            
            System.out.println("✅ Building order request - Restaurant ID: " + restaurantId + ", Food IDs count: " + foodIds.length);
            
            // Create order request
            OrderRequest orderRequest = new OrderRequest();
            orderRequest.setUserId(userId);
            orderRequest.setResId(restaurantId);
            orderRequest.setFoodIds(foodIds);
            
            // Create order
            boolean orderCreated = insertOrder(orderRequest);
            
            if (orderCreated) {
                System.out.println("✅ Order created successfully");
                
                // Clear cart after successful order
                boolean cartCleared = cartService.clearCart(userId);
                if (cartCleared) {
                    System.out.println("✅ Cart cleared after order");
                } else {
                    System.out.println("⚠️ Failed to clear cart after order");
                }
                
                return true;
            } else {
                System.err.println("❌ Failed to create order");
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ Error in checkoutFromCart: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
