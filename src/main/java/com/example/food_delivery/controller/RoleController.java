package com.example.food_delivery.controller;

import com.example.food_delivery.dto.request.RoleRequest;
import com.example.food_delivery.dto.response.ApiResponse;
import com.example.food_delivery.dto.response.RoleResponse;
import com.example.food_delivery.service.imp.RoleServiceImp;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleServiceImp roleService;

    @PostMapping
    ApiResponse<RoleResponse> create(@RequestBody RoleRequest request) {
        var resp = new ApiResponse();
        resp.setResult(roleService.create(request));
        return resp;
    }

    @GetMapping
    ApiResponse<List<RoleResponse>> getAll() {
        var resp = new ApiResponse();
        resp.setResult(roleService.getAll());
        return resp;
    }

    @DeleteMapping("/{role}")
    ApiResponse<Void> delete(@PathVariable String role) {
        roleService.delete(role);
        return new ApiResponse();
    }
}
