package com.FarmVibeApplication.api.Repository;

import com.FarmVibeApplication.api.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByDeliveryStatus(String deliveryStatus);
}