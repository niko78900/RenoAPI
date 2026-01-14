package com.example.HomeReno.controller;

import com.example.HomeReno.controller.dto.AuthRequest;
import com.example.HomeReno.controller.dto.AuthResponse;
import com.example.HomeReno.controller.dto.UserResponse;
import com.example.HomeReno.entity.User;
import com.example.HomeReno.security.JwtService;
import com.example.HomeReno.service.AuthService;
import com.example.HomeReno.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {
    private final UserService userService;
    private final AuthService authService;
    private final JwtService jwtService;

    @Autowired
    public AuthController(UserService userService, AuthService authService, JwtService jwtService) {
        this.userService = userService;
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody AuthRequest request) {
        if (request == null || request.username() == null || request.password() == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            User user = userService.register(request.username(), request.password());
            return ResponseEntity.ok(toUserResponse(user));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        if (request == null || request.username() == null || request.password() == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            User user = authService.authenticate(request.username(), request.password());
            String token = jwtService.generateToken(user);
            return ResponseEntity.ok(new AuthResponse(token, "Bearer", user.getUsername(), user.getRole().name()));
        } catch (DisabledException ex) {
            return ResponseEntity.status(403).build();
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
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
