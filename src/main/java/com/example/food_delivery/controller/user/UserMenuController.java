package com.example.food_delivery.controller.user;

import com.example.food_delivery.dto.response.ResponseData;
import com.example.food_delivery.service.imp.FileServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController("userMenuController")
@RequestMapping("/menu")
public class UserMenuController {

    @Autowired
    FileServiceImp fileServiceImp;

    /**
     * GET /menu - Lấy danh sách tất cả món ăn
     * Public endpoint - không cần authentication
     */
    @GetMapping()
    public ResponseEntity<?> getAllMenus() {
        ResponseData responseData = new ResponseData();
        try {
            // Note: Cần thêm method getAllMenus vào MenuService
            responseData.setStatus(501);
            responseData.setSuccess(false);
            responseData.setData(null);
            responseData.setDesc("Chức năng lấy danh sách món ăn chưa được triển khai. Cần thêm method getAllMenus vào MenuService");
            return new ResponseEntity<>(responseData, HttpStatus.NOT_IMPLEMENTED);
        } catch (Exception e) {
            responseData.setStatus(500);
            responseData.setSuccess(false);
            responseData.setData(null);
            responseData.setDesc("Lỗi khi lấy danh sách món ăn: " + e.getMessage());
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * GET /menu/{id} - Lấy món ăn theo ID
     * Public endpoint - không cần authentication
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getMenuById(@PathVariable int id) {
        ResponseData responseData = new ResponseData();
        try {
            // Validate input
            if (id <= 0) {
                responseData.setStatus(400);
                responseData.setSuccess(false);
                responseData.setData(null);
                responseData.setDesc("Menu ID không hợp lệ!");
                return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
            }
            
            // Note: Cần thêm method getMenuById vào MenuService
            responseData.setStatus(501);
            responseData.setSuccess(false);
            responseData.setData(null);
            responseData.setDesc("Chức năng lấy món ăn theo ID chưa được triển khai. Cần thêm method getMenuById vào MenuService");
            return new ResponseEntity<>(responseData, HttpStatus.NOT_IMPLEMENTED);
        } catch (Exception e) {
            responseData.setStatus(500);
            responseData.setSuccess(false);
            responseData.setData(null);
            responseData.setDesc("Lỗi khi lấy món ăn: " + e.getMessage());
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * GET /menu/file/{filename} - Lấy file ảnh của món ăn
     * Public endpoint - không cần authentication
     */
    @GetMapping("/file/{filename:.+}")
    public ResponseEntity<?> getMenuFile(@PathVariable String filename) {
        try {
            Resource resource = fileServiceImp.loadFile(filename);
            if (resource == null || !resource.exists()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            ResponseData responseData = new ResponseData();
            responseData.setStatus(404);
            responseData.setSuccess(false);
            responseData.setData(null);
            responseData.setDesc("Không tìm thấy file: " + filename);
            return new ResponseEntity<>(responseData, HttpStatus.NOT_FOUND);
        }
    }
}

