package com.FarmVibeApplication.api.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "user_details")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long userId;


    @Column(name = "user_firstname")
    private String user_firstName;

    @Column(name = "user_lastname")
    private String user_lastName;

    @Column(name = "user_email")
    private String user_email;

    @Column(name = "mobile_Number")
    private Long mobileNumber;

    @Column(name = "user_password")
    private String user_password;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "role")
    private String role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Address> addresses;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders;

    @PrePersist
    protected void onCreate() {
        if (this.role == null) {
            this.role = "USER";
        }
    }

    public String getFullName() {
        return user_firstName + " " + user_lastName;
    }



}
