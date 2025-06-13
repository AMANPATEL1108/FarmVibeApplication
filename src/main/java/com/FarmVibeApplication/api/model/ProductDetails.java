package com.FarmVibeApplication.api.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "product_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    private double price;

    private String weight;

    private int quantity;

    private int stock;

    // Store benefits as JSON string in DB
    @Column(columnDefinition = "TEXT")
    private String benefits;

    // === Transient field to work with benefits as a List in Java ===
    @Transient
    public List<String> getBenefitList() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(this.benefits, List.class);
        } catch (JsonProcessingException e) {
            return List.of(); // return empty list if error
        }
    }

    @Transient
    public void setBenefitList(List<String> benefitList) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            this.benefits = objectMapper.writeValueAsString(benefitList);
        } catch (JsonProcessingException e) {
            this.benefits = "[]"; // fallback
        }
    }

    // Many products belong to one category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}
