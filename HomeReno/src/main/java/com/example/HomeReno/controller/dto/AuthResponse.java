package com.example.HomeReno.controller.dto;

public record AuthResponse(String token, String tokenType, String username, String role) {}
