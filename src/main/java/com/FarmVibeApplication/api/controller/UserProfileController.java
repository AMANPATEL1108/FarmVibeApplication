package com.FarmVibeApplication.api.controller;


import com.FarmVibeApplication.api.Repository.UserRepository;
import com.FarmVibeApplication.api.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
import java.util.Optional;

@Controller
public class UserProfileController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/user/update-profile")
    @ResponseBody
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> payload) {
        try {
            Long userId = Long.parseLong(payload.get("userId"));
            String name = payload.get("name");
            String email = payload.get("email");
            String phone = payload.get("phone");
            String address = payload.get("address");

            Optional<User> optionalUser = userRepository.findById(userId);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            User user = optionalUser.get();

            // Update basic fields
            if (name.contains(" ")) {
                String[] parts = name.split(" ", 2);
                user.setUser_firstName(parts[0]);
                user.setUser_lastName(parts[1]);
            } else {
                user.setUser_firstName(name);
            }
            user.setUser_email(email);
            user.setMobileNumber(Long.parseLong(phone));

            // Update address (simplified to first one only)
            if (!user.getAddresses().isEmpty()) {
                String[] addrParts = address.split(",", 3);
                user.getAddresses().get(0).setHouse_number(addrParts.length > 0 ? addrParts[0].trim() : "");
                user.getAddresses().get(0).setStreet(addrParts.length > 1 ? addrParts[1].trim() : "");
                user.getAddresses().get(0).setPincode(addrParts.length > 2 ? addrParts[2].trim() : "");
            }

            userRepository.save(user);
            return ResponseEntity.ok("Profile updated");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update");
        }
    }

}
