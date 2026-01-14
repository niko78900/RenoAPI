package com.example.HomeReno.controller;

import com.example.HomeReno.controller.dto.UserResponse;
import com.example.HomeReno.entity.User;
import com.example.HomeReno.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = "http://localhost:4200")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {
    private final UserService userService;

    @Autowired
    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/pending")
    public List<UserResponse> getPendingUsers() {
        return userService.getPendingUsers()
                .stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<UserResponse> approveUser(@PathVariable String id) {
        try {
            User user = userService.approveUser(id);
            return ResponseEntity.ok(toUserResponse(user));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getRole().name(),
                user.isEnabled(),
                user.getCreatedAt()
        );
    }
}
