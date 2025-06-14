package com.FarmVibeApplication.api.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    private int quantity;
    private double totalPrice;
    private LocalDate orderDate;
    private LocalDate deliveryDate;
    private String paymentMethod;
    private String paymentStatus;
    private String deliveryStatus;

    // ✅ ADD THIS FIELD
    private boolean orderConfirmed;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductDetails product;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
