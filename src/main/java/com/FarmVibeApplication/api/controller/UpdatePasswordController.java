package com.FarmVibeApplication.api.controller;

import com.FarmVibeApplication.api.Repository.UserRepository;
import com.FarmVibeApplication.api.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
public class UpdatePasswordController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/update-password")
    public Map<String, Object> updatePassword(@RequestBody Map<String, String> body) {
        String mobileStr = body.get("mobile");
        String newPassword = body.get("password");

        if (mobileStr == null || newPassword == null) {
            return Map.of("success", false, "message", "Missing mobile or password");
        }

        Long mobile;
        try {
            mobile = Long.parseLong(mobileStr);
        } catch (NumberFormatException e) {
            return Map.of("success", false, "message", "Invalid mobile format");
        }

        Optional<User> userOpt = userRepository.findByMobileNumber(mobile);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setUser_password(newPassword);
            userRepository.save(user);
            return Map.of("success", true);
        }

        return Map.of("success", false, "message", "User not found");
    }
}
