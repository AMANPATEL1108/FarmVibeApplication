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
//    private int quantity;
    private int stock;

    @Column(columnDefinition = "TEXT")
    private String benefits;

    @Transient
    public List<String> getBenefitList() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(this.benefits, List.class);
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }

    @Transient
    public void setBenefitList(List<String> benefitList) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            this.benefits = objectMapper.writeValueAsString(benefitList);
        } catch (JsonProcessingException e) {
            this.benefits = "[]";
        }
    }

    // Many products belong to one category
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Order> orders;

}
