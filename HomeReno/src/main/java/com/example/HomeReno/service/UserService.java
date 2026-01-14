package com.example.HomeReno.service;

import com.example.HomeReno.entity.User;
import com.example.HomeReno.entity.UserRole;
import com.example.HomeReno.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String username, String password) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username is required");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("password is required");
        }
        String trimmedUsername = username.trim();
        if (userRepository.existsByUsername(trimmedUsername)) {
            throw new IllegalArgumentException("username already exists");
        }
        String hashedPassword = passwordEncoder.encode(password);
        User user = new User(trimmedUsername, hashedPassword, UserRole.USER, false);
        return userRepository.save(user);
    }

    public User approveUser(String userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    user.setEnabled(true);
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<User> getPendingUsers() {
        return userRepository.findByEnabledFalse();
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
