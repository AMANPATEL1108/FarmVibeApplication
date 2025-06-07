package com.FarmVibeApplication.api.Dao;


import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    String registerUser(String firstName, String lastName, String email, Long phone, String password, MultipartFile profileImage);
}
