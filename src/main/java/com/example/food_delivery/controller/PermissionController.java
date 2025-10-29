package com.example.food_delivery.controller;


import com.example.food_delivery.dto.request.PermissionRequest;
import com.example.food_delivery.dto.response.ApiResponse;
import com.example.food_delivery.dto.response.PermissionResponse;
import com.example.food_delivery.service.imp.PermissionServiceImp;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {
    PermissionServiceImp permissionService;

    @PostMapping
    ApiResponse<PermissionResponse> create(@RequestBody PermissionRequest permissionRequest) {
        var resp = new ApiResponse();
        resp.setResult(permissionService.create(permissionRequest));
        return resp;
    }

    @GetMapping
    ApiResponse<List<PermissionResponse>> getAll() {
        var resp = new ApiResponse();
        resp.setResult(permissionService.getAll());
        return resp;
    }

    @DeleteMapping("/{permission}")
    ApiResponse<Void> delete(@PathVariable String permission) {
        permissionService.delete(permission);
        return new ApiResponse();
    }
}
