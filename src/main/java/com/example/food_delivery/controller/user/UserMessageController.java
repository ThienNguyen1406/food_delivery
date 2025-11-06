package com.example.food_delivery.controller.user;

import com.example.food_delivery.domain.entity.Message;
import com.example.food_delivery.domain.entity.Users;
import com.example.food_delivery.dto.request.SendMessageRequest;
import com.example.food_delivery.dto.response.MessageDTO;
import com.example.food_delivery.dto.response.ConversationDTO;
import com.example.food_delivery.dto.response.ResponseData;
import com.example.food_delivery.service.MessageService;
import com.example.food_delivery.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController("userMessageController")
@RequestMapping("/message")
public class UserMessageController {

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    /**
     * GET /message - Lấy danh sách conversations
     * - Nếu là USER: trả về admin (người duy nhất có thể chat)
     * - Nếu là ADMIN: trả về danh sách users đã chat
     * Yêu cầu authentication
     */
    @GetMapping()
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getConversations(@RequestParam(required = false) Integer userId) {
        System.out.println("=== GET /message called ===");
        System.out.println("Request userId param: " + userId);
        
        // Check authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication: " + (authentication != null ? "exists" : "null"));
        if (authentication != null) {
            System.out.println("Is authenticated: " + authentication.isAuthenticated());
            System.out.println("Principal: " + authentication.getPrincipal());
            System.out.println("Authorities: " + authentication.getAuthorities());
        }
        
        ResponseData responseData = new ResponseData();
        try {
            
            // Nếu không có userId, lấy từ authenticated user
            int currentUserId = userId != null ? userId : getCurrentUserId();
            System.out.println("Current user ID: " + currentUserId);
            if (currentUserId <= 0) {
                responseData.setStatus(400);
                responseData.setSuccess(false);
                responseData.setData(null);
                responseData.setDesc("User ID không hợp lệ!");
                return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
            }

            // Kiểm tra role của user hiện tại
            var currentUserDTO = userService.getMyInfo();
            boolean isAdmin = currentUserDTO != null && "ADMIN".equalsIgnoreCase(currentUserDTO.getRoleName());
            
            List<Users> conversations;
            Users adminUser = messageService.getAdminUser();
            
            if (isAdmin) {
                // Admin: lấy danh sách users đã chat
                System.out.println("Admin detected, getting users for admin...");
                conversations = messageService.getUsersForAdmin(currentUserId);
                System.out.println("Found " + conversations.size() + " conversations for admin");
                // Nếu chưa có conversation nào, trả về danh sách rỗng
            } else {
                // User: chỉ chat với admin
                conversations = new ArrayList<>();
                if (adminUser != null) {
                    conversations.add(adminUser);
                }
            }
            
            List<Message> lastMessages = messageService.getLastMessages(currentUserId);
            
            // Convert to ConversationDTO
            List<ConversationDTO> conversationDTOs = new ArrayList<>();
            Map<Integer, Message> lastMessageMap = new HashMap<>();
            for (Message msg : lastMessages) {
                int otherUserId = msg.getSender().getId() == currentUserId ? msg.getReceiver().getId() : msg.getSender().getId();
                lastMessageMap.put(otherUserId, msg);
            }
            
            for (Users user : conversations) {
                Message lastMsg = lastMessageMap.get(user.getId());
                // Đếm unread messages cho conversation này
                long unreadCount = 0;
                if (isAdmin) {
                    // Admin: đếm tin nhắn chưa đọc từ user này
                    List<Message> conversation = messageService.getConversation(currentUserId, user.getId());
                    unreadCount = conversation.stream()
                            .filter(msg -> msg.getReceiver().getId() == currentUserId && !msg.isRead())
                            .count();
                } else {
                    // User: đếm tin nhắn chưa đọc từ admin
                    List<Message> conversation = messageService.getConversation(currentUserId, user.getId());
                    unreadCount = conversation.stream()
                            .filter(msg -> msg.getReceiver().getId() == currentUserId && !msg.isRead())
                            .count();
                }
                
                ConversationDTO dto = ConversationDTO.builder()
                        .userId(user.getId())
                        .userName(user.getUserName())
                        .fullName(user.getFullName())
                        .lastMessage(lastMsg != null ? lastMsg.getContent() : "")
                        .lastMessageDate(lastMsg != null ? lastMsg.getCreateDate() : null)
                        .unreadCount(unreadCount)
                        .build();
                conversationDTOs.add(dto);
            }
            
            System.out.println("Returning " + conversationDTOs.size() + " conversation DTOs");
            responseData.setStatus(200);
            responseData.setSuccess(true);
            responseData.setData(conversationDTOs);
            responseData.setDesc("Lấy danh sách conversations thành công!");
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error getting conversations: " + e.getMessage());
            e.printStackTrace();
            responseData.setStatus(500);
            responseData.setSuccess(false);
            responseData.setData(null);
            responseData.setDesc("Lỗi server khi lấy danh sách conversations: " + e.getMessage());
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * GET /message/{otherUserId} - Lấy tin nhắn giữa user hiện tại và otherUserId
     * Yêu cầu authentication
     */
    @GetMapping("/{otherUserId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getConversation(@PathVariable int otherUserId, @RequestParam(required = false) Integer userId) {
        ResponseData responseData = new ResponseData();
        try {
            int currentUserId = userId != null ? userId : getCurrentUserId();
            if (currentUserId <= 0 || otherUserId <= 0) {
                responseData.setStatus(400);
                responseData.setSuccess(false);
                responseData.setData(null);
                responseData.setDesc("User ID không hợp lệ!");
                return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
            }

            List<Message> messages = messageService.getConversation(currentUserId, otherUserId);
            
            // Convert to MessageDTO
            List<MessageDTO> messageDTOs = new ArrayList<>();
            for (Message msg : messages) {
                MessageDTO dto = MessageDTO.builder()
                        .id(msg.getId())
                        .senderId(msg.getSender().getId())
                        .senderName(msg.getSender().getFullName())
                        .senderUserName(msg.getSender().getUserName())
                        .receiverId(msg.getReceiver().getId())
                        .receiverName(msg.getReceiver().getFullName())
                        .receiverUserName(msg.getReceiver().getUserName())
                        .content(msg.getContent())
                        .isRead(msg.isRead())
                        .createDate(msg.getCreateDate())
                        .build();
                messageDTOs.add(dto);
            }
            
            // Đánh dấu đã đọc
            messageService.markConversationAsRead(currentUserId, otherUserId);
            
            responseData.setStatus(200);
            responseData.setSuccess(true);
            responseData.setData(messageDTOs);
            responseData.setDesc("Lấy tin nhắn thành công!");
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error getting conversation: " + e.getMessage());
            e.printStackTrace();
            responseData.setStatus(500);
            responseData.setSuccess(false);
            responseData.setData(null);
            responseData.setDesc("Lỗi server khi lấy tin nhắn: " + e.getMessage());
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * POST /message - Gửi tin nhắn
     * - Nếu là USER: tự động gửi đến admin (bỏ qua receiverId trong request)
     * - Nếu là ADMIN: gửi đến receiverId chỉ định
     * Yêu cầu authentication
     */
    @PostMapping()
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> sendMessage(@RequestBody SendMessageRequest request, @RequestParam(required = false) Integer userId) {
        ResponseData responseData = new ResponseData();
        try {
            int currentUserId = userId != null ? userId : getCurrentUserId();
            if (currentUserId <= 0) {
                responseData.setStatus(400);
                responseData.setSuccess(false);
                responseData.setData(null);
                responseData.setDesc("User ID không hợp lệ!");
                return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
            }

            if (request.getContent() == null || request.getContent().trim().isEmpty()) {
                responseData.setStatus(400);
                responseData.setSuccess(false);
                responseData.setData(null);
                responseData.setDesc("Nội dung tin nhắn không được để trống!");
                return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
            }

            // Kiểm tra role của user hiện tại
            var currentUserDTO = userService.getMyInfo();
            boolean isAdmin = currentUserDTO != null && "ADMIN".equalsIgnoreCase(currentUserDTO.getRoleName());
            
            int receiverId;
            if (isAdmin) {
                // Admin: gửi đến receiverId chỉ định
                if (request.getReceiverId() <= 0) {
                    responseData.setStatus(400);
                    responseData.setSuccess(false);
                    responseData.setData(null);
                    responseData.setDesc("Receiver ID không hợp lệ!");
                    return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
                }
                receiverId = request.getReceiverId();
            } else {
                // User: tự động gửi đến admin
                Users adminUser = messageService.getAdminUser();
                if (adminUser == null) {
                    responseData.setStatus(400);
                    responseData.setSuccess(false);
                    responseData.setData(null);
                    responseData.setDesc("Không tìm thấy admin để gửi tin nhắn!");
                    return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
                }
                receiverId = adminUser.getId();
            }

            Message message = messageService.sendMessage(currentUserId, receiverId, request.getContent().trim());
            if (message == null) {
                responseData.setStatus(400);
                responseData.setSuccess(false);
                responseData.setData(null);
                responseData.setDesc("Gửi tin nhắn thất bại!");
                return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
            }

            MessageDTO dto = MessageDTO.builder()
                    .id(message.getId())
                    .senderId(message.getSender().getId())
                    .senderName(message.getSender().getFullName())
                    .senderUserName(message.getSender().getUserName())
                    .receiverId(message.getReceiver().getId())
                    .receiverName(message.getReceiver().getFullName())
                    .receiverUserName(message.getReceiver().getUserName())
                    .content(message.getContent())
                    .isRead(message.isRead())
                    .createDate(message.getCreateDate())
                    .build();

            responseData.setStatus(200);
            responseData.setSuccess(true);
            responseData.setData(dto);
            responseData.setDesc("Gửi tin nhắn thành công!");
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
            e.printStackTrace();
            responseData.setStatus(500);
            responseData.setSuccess(false);
            responseData.setData(null);
            responseData.setDesc("Lỗi server khi gửi tin nhắn: " + e.getMessage());
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * GET /message/unread/count - Lấy số lượng tin nhắn chưa đọc
     * Yêu cầu authentication
     */
    @GetMapping("/unread/count")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getUnreadCount(@RequestParam(required = false) Integer userId) {
        ResponseData responseData = new ResponseData();
        try {
            int currentUserId = userId != null ? userId : getCurrentUserId();
            if (currentUserId <= 0) {
                responseData.setStatus(400);
                responseData.setSuccess(false);
                responseData.setData(null);
                responseData.setDesc("User ID không hợp lệ!");
                return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
            }

            long count = messageService.getUnreadCount(currentUserId);
            Map<String, Long> result = new HashMap<>();
            result.put("count", count);

            responseData.setStatus(200);
            responseData.setSuccess(true);
            responseData.setData(result);
            responseData.setDesc("Lấy số lượng tin nhắn chưa đọc thành công!");
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error getting unread count: " + e.getMessage());
            e.printStackTrace();
            responseData.setStatus(500);
            responseData.setSuccess(false);
            responseData.setData(null);
            responseData.setDesc("Lỗi server khi lấy số lượng tin nhắn: " + e.getMessage());
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Helper method để lấy userId từ authenticated user
     */
    private int getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("Authentication: " + (authentication != null ? "exists" : "null"));
            if (authentication != null) {
                System.out.println("Is authenticated: " + authentication.isAuthenticated());
                System.out.println("Principal: " + authentication.getPrincipal());
                System.out.println("Authorities: " + authentication.getAuthorities());
            }
            
            if (authentication == null || !authentication.isAuthenticated()) {
                System.err.println("Authentication is null or not authenticated");
                return 0;
            }
            String username = authentication.getName();
            System.out.println("Username from authentication: " + username);
            if (username == null || username.isEmpty()) {
                System.err.println("Username is null or empty");
                return 0;
            }
            var userDTO = userService.getMyInfo();
            if (userDTO != null) {
                System.out.println("User DTO found: ID=" + userDTO.getId() + ", Name=" + userDTO.getFullName());
                return userDTO.getId();
            }
            System.err.println("User DTO is null");
            return 0;
        } catch (Exception e) {
            System.err.println("Error getting current user ID: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
}

