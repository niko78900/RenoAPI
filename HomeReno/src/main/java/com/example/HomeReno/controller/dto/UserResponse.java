package com.example.HomeReno.controller.dto;

import java.time.LocalDateTime;

public record UserResponse(
        String id,
        String username,
        String role,
        boolean enabled,
        LocalDateTime createdAt
) {}
