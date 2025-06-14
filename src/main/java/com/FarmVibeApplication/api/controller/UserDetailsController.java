package com.FarmVibeApplication.api.controller;

import com.FarmVibeApplication.api.Repository.UserRepository;
import com.FarmVibeApplication.api.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserDetailsController {

    @Autowired
    private UserRepository userRepository;

    // ✅ Load user profile by userId
    @GetMapping("/user/profile")
    public String getUserProfile(@RequestParam("userId") Long userId, Model model) {
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            // handle user not found
            return "redirect:/error"; // or a custom error page
        }

        model.addAttribute("user", user);
        model.addAttribute("addresses", user.getAddresses());
        model.addAttribute("contentPage", "userPages/profile");
        return "usermaster";
    }
}
