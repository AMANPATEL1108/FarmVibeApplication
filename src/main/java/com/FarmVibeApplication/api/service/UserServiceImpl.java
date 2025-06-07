package com.FarmVibeApplication.api.service;

import com.FarmVibeApplication.api.Dao.UserService;
import com.FarmVibeApplication.api.Repository.UserRepository;
import com.FarmVibeApplication.api.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public String registerUser(String firstName, String lastName, String email, Long phone, String password, MultipartFile profileImage) {
        try {
            String uploadDir = "C:/Users/amanp/OneDrive/Desktop/aman/Project/Application Project/FarmVibe/src/main/resources/static/images/uploadedUserImages/";

            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFileName = profileImage.getOriginalFilename();
            String fileExtension = "";

            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                originalFileName = originalFileName.substring(0, originalFileName.lastIndexOf("."));
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
            String timestamp = LocalDateTime.now().format(formatter);

            String fileName = timestamp + "_" + originalFileName + fileExtension;

            Path filePath = uploadPath.resolve(fileName);

            Files.write(filePath, profileImage.getBytes());

            String imageUrl = "/images/uploadedUserImages/" + fileName;
            User user = new User();
            user.setUser_firstName(firstName);
            user.setUser_lastName(lastName);
            user.setUser_email(email);
            user.setMobileNumber(phone);
            user.setUser_password(password);
            user.setProfileImageUrl(imageUrl);
            user.setRole("USER");


            userRepository.save(user);
            return "redirect:/pageUrl?page=signing";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }


}
