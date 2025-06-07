package com.FarmVibeApplication.api.service;

import com.FarmVibeApplication.api.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    private final String secretKey = "sdfjsdbf12313kdbsufov324242bdknfvkdzb24fudgkndf@##$%k34@#$gn@#34$kdfjbvkd12f@#$%bxgkdfw34w34"; // You should store this securely

    public String generateToken(User user) {
        long expirationTime = 1000 * 60 * 60 * 10; // 10 hours

        return Jwts.builder()
                .setSubject(user.getUser_firstName()) // You can store userId or any unique info
                .claim("mobile", user.getMobileNumber())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes()) // << ✅ use getBytes() instead of DatatypeConverter
                .compact();
    }
}
