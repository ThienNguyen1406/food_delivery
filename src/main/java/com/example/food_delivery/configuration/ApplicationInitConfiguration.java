package com.example.food_delivery.configuration;

import com.example.food_delivery.domain.entity.Roles;
import com.example.food_delivery.domain.entity.Users;
import com.example.food_delivery.reponsitory.RoleRepository;
import com.example.food_delivery.reponsitory.UserReponsitory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
@Order(2)
public class ApplicationInitConfiguration implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final UserReponsitory userRepository;
    private final RoleRepository roleRepository;
    @Value("${user-admin.email}")
    private String adminEmail;

    @Value("${user-admin.password}")
    private String adminPassword;

    public ApplicationInitConfiguration(PasswordEncoder passwordEncoder, UserReponsitory userRepository, RoleRepository roleRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Users user = userRepository.findByUserName(adminEmail);
        if (user == null) {
            user = Users.builder()
                    .userName(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .build();
            userRepository.save(user);
        }
        userRepository.save(user);

        List<Roles> roles = roleRepository.findAll();

        List<Roles> userRoles = roleRepository.findAllByUserId(user.getId());
        Set<String> existingRoleIds = userRoles.stream()
                .map(Roles::getRoleName)
                .collect(Collectors.toSet());

        List<Roles> newUserRoles = roles.stream()
                .filter(role -> !existingRoleIds.contains(role.getRoleName())) // Lọc các role chưa có
                .map(role -> Roles.builder().roleName(role.getRoleName()).build()) // Tạo UserRole mới
                .toList();

        if (!newUserRoles.isEmpty()) {
            roleRepository.saveAll(newUserRoles);
        }
    }
}
