package com.FarmVibeApplication.api.controller;

import com.FarmVibeApplication.api.Repository.UserRepository;
import com.FarmVibeApplication.api.model.User;
import com.FarmVibeApplication.api.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam("phone") String phone,
                                   @RequestParam("password") String password) {
        // Remove +91 if present
        if (phone.startsWith("+91")) {
            phone = phone.substring(3);
        }

        Long phoneNumber;
        try {
            phoneNumber = Long.parseLong(phone);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("{\"error\":\"Invalid phone number format.\"}");
        }

        Optional<User> userOptional = userRepository.findByMobileNumber(phoneNumber);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(401).body("{\"error\":\"User not found\"}");
        }

        User user = userOptional.get();
        if (!user.getUser_password().equals(password)) {
            return ResponseEntity.status(401).body("{\"error\":\"Invalid password\"}");
        }

        String token = jwtUtil.generateToken(user);
        return ResponseEntity.ok("{\"token\":\"" + token + "\"}");
    }
}
