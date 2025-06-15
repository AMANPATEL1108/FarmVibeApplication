package com.FarmVibeApplication.api.controller;

import com.FarmVibeApplication.api.Repository.AddressRepository;
import com.FarmVibeApplication.api.Repository.UserRepository;
import com.FarmVibeApplication.api.model.Address;
import com.FarmVibeApplication.api.model.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Controller
public class UserProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping("/user/upload-profile-image")
    @ResponseBody
    public ResponseEntity<?> uploadProfileImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Please select a file to upload");
            }

            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate safe filename
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss")) +
                    "_" + UUID.randomUUID() + fileExtension;

            // Save file
            Path filePath = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Return relative path
            String relativePath = "/images/uploadedUserImages/" + newFilename;
            return ResponseEntity.ok(relativePath);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload image: " + e.getMessage());
        }
    }

    @PostMapping("/user/update-profile-image")
    @ResponseBody
    @Transactional
    public ResponseEntity<?> updateProfileImage(@RequestBody Map<String, String> payload) {
        try {
            String userIdStr = payload.get("userId");
            String profileImage = payload.get("profile_image");

            if (userIdStr == null || profileImage == null) {
                return ResponseEntity.badRequest().body("Missing required fields");
            }

            Long userId = Long.parseLong(userIdStr);
            userRepository.updateProfileImage(userId, profileImage);

            return ResponseEntity.ok("Profile image updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update profile image");
        }
    }

    @PostMapping("/user/remove-profile-image")
    @ResponseBody
    @Transactional
    public ResponseEntity<?> removeProfileImage(@RequestBody Map<String, String> payload) {
        try {
            String userIdStr = payload.get("userId");
            if (userIdStr == null) {
                return ResponseEntity.badRequest().body("User ID is required");
            }

            Long userId = Long.parseLong(userIdStr);
            userRepository.updateProfileImage(userId, null);

            return ResponseEntity.ok("Profile image removed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to remove profile image");
        }
    }

    @PostMapping("/user/update-profile")
    @ResponseBody
    @Transactional
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> payload) {
        try {
            String userIdStr = payload.get("userId");
            String firstName = payload.get("first_name");
            String lastName = payload.get("last_name");
            String email = payload.get("email");
            String phone = payload.get("phone");
            String profileImage = payload.get("profile_image");

            // Validate all required fields
            if (userIdStr == null || firstName == null || lastName == null ||
                    email == null || phone == null) {
                return ResponseEntity.badRequest().body("Missing required fields");
            }

            Long userId = Long.parseLong(userIdStr);
            Long phoneNumber = Long.parseLong(phone);

            // Use the repository's update method
            int updated = userRepository.updateUserProfile(
                    userId,
                    firstName.trim(),
                    lastName.trim(),
                    email.trim(),
                    phoneNumber,
                    profileImage != null ? profileImage.trim() : null
            );

            if (updated == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            return ResponseEntity.ok("Profile updated successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update profile: " + e.getMessage());
        }
    }

    // Rest of your existing controller methods...


    @GetMapping("/user/get-address/{addressId}")
    @ResponseBody
    public ResponseEntity<Address> getAddress(@PathVariable Long addressId) {
        return addressRepository.findById(addressId)
                .map(address -> new Address(
                        address.getId(),
                        address.getFirst_name(),
                        address.getLast_name(),
                        address.getEmail(),
                        address.getNumber(),
                        address.getStreet(),
                        address.getCity(),
                        address.getArea(),
                        address.getHouse_number(),
                        address.getPincode()
                ))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/user/update-address")
    @ResponseBody
    @Transactional
    public ResponseEntity<?> updateAddress(@RequestBody Address addressDTO) {
        try {
            if (addressDTO.getId() == null) {
                return ResponseEntity.badRequest().body("Address ID is required");
            }

            int updated = addressRepository.updateAddress(
                    addressDTO.getId(),
                    addressDTO.getFirst_name(),
                    addressDTO.getLast_name(),
                    addressDTO.getEmail(),
                    addressDTO.getNumber(),
                    addressDTO.getHouse_number(),
                    addressDTO.getStreet(),
                    addressDTO.getCity(),
                    addressDTO.getArea(),
                    addressDTO.getPincode()
            );

            if (updated == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Address not found");
            }

            return ResponseEntity.ok("Address updated successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update address");
        }
    }

    @PostMapping("/user/delete-address")
    @ResponseBody
    @Transactional
    public ResponseEntity<?> deleteAddress(@RequestBody Map<String, Long> payload) {
        try {
            Long addressId = payload.get("addressId");
            if (addressId == null) {
                return ResponseEntity.badRequest().body("Address ID is required");
            }

            addressRepository.deleteById(addressId);
            return ResponseEntity.ok("Address deleted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete address");
        }
    }
}
