package com.FarmVibeApplication.api.controller;

import com.FarmVibeApplication.api.Dao.UserService;
import com.FarmVibeApplication.api.model.User;
import com.FarmVibeApplication.api.service.JwtService;
import com.FarmVibeApplication.api.service.OtpService;
import com.FarmVibeApplication.api.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.ResponseEntity;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    private ConcurrentHashMap<String, Boolean> verifiedPhones = new ConcurrentHashMap<>();

    @PostMapping("/send-otp")
    @ResponseBody
    public String sendOtp(@RequestParam("phone") String phone) {
        if (!phone.startsWith("+91")) {
            phone = "+91" + phone;
        }
        otpService.generateOtp(phone);
        verifiedPhones.remove(phone);
        return "OTP sent successfully";
    }

    @PostMapping("/verify-otp")
    @ResponseBody
    public String verifyOtp(@RequestParam("phone") String phone, @RequestParam("otp") String otp) {
        if (!phone.startsWith("+91")) {
            phone = "+91" + phone;
        }
        boolean isValid = otpService.validateOtp(phone, otp);
        if (isValid) {
            verifiedPhones.put(phone, true);
        }
        System.out.println("OTP validation confirmation: " + isValid);
        return isValid ? "{\"success\":true}" : "{\"success\":false}";
    }

    @PostMapping("/user-register")
    public String registerUser(@RequestParam("firstName") String firstName,
                               @RequestParam("lastName") String lastName,
                               @RequestParam("email") String email,
                               @RequestParam("phone") String phone,
                               @RequestParam("password") String password,
                               @RequestParam("profileImage") MultipartFile profileImage) {
        if (!phone.startsWith("+91")) {
            phone = "+91" + phone;
        }

        if (verifiedPhones.getOrDefault(phone, false)) {
            String result = userService.registerUser(firstName, lastName, email, Long.parseLong(phone.replace("+91", "")), password, profileImage);
            verifiedPhones.remove(phone);
            return result;
        } else {
            return "redirect:/pageUrl?page=register&error=Invalid OTP";
        }
    }

    // ✅ Login only through mobile number
    // ✅ Login only through mobile number
    @PostMapping("/user-login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestParam("mobile") String mobileStr,
                                   @RequestParam("password") String password) {
        if (mobileStr == null || !mobileStr.matches("^[6-9]\\d{9}$")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid phone number format."));
        }

        Long mobile;
        try {
            mobile = Long.parseLong(mobileStr);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid phone number format."));
        }

        Optional<User> userOptional = userRepository.findByMobileNumber(mobile);
        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid credentials."));
        }

        User user = userOptional.get();

        if (!user.getUser_password().equals(password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid credentials."));
        }

        String token = jwtService.generateToken(user);

        // ✅ Include userId in response
        return ResponseEntity.ok(Map.of(
                "token", token,
                "userId", user.getUserId(),
                "username", user.getUser_firstName()
        ));
    }




}
