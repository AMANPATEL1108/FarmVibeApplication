package com.FarmVibeApplication.api.Repository;

import com.FarmVibeApplication.api.model.ProductDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductDetails, Long> {
    List<ProductDetails> findAll();
}
