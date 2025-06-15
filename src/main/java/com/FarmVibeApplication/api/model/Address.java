package com.FarmVibeApplication.api.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "address")
@Data
@NoArgsConstructor
//@Builder(toBuilder = true)
public class Address {

    public Address(Long id, String first_name, String last_name, String email, String number, String street, String city, String area, String house_number, String pincode) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.number = number;
        this.street = street;
        this.city = city;
        this.area = area;
        this.house_number = house_number;
        this.pincode = pincode;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String first_name;

    @Column(name = "last_name")
    private String last_name;

    private String email;

    private String number;

    private String street;

    private String city;       // ✅ Added new field

    private String area;       // ✅ Added new field

    @Column(name = "house_number")
    private String house_number;

    private String pincode;

    // Foreign Key to User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // ✅ Explicit getters and setters for Thymeleaf compatibility (optional if Lombok works properly)

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getHouse_number() {
        return house_number;
    }

    public void setHouse_number(String house_number) {
        this.house_number = house_number;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
