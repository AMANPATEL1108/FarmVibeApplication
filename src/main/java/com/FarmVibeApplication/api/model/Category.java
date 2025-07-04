package com.FarmVibeApplication.api.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String category_image_url;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<ProductDetails> products;

}