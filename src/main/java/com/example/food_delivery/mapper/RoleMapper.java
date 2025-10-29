package com.example.food_delivery.mapper;


import com.example.food_delivery.domain.entity.Roles;
import com.example.food_delivery.dto.request.RoleRequest;
import com.example.food_delivery.dto.response.RoleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    Roles toRole(RoleRequest request);

    RoleResponse toRoleResponse(Roles role);
}
