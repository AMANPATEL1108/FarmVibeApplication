package com.FarmVibeApplication.api.Repository;

import com.FarmVibeApplication.api.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByDeliveryStatus(String status);
    List<Order> findByDeliveryDateAndDeliveryStatus(LocalDate deliveryDate, String deliveryStatus);
    List<Order> findByDeliveryDate(LocalDate deliveryDate);
    List<Order> findByUser_UserIdAndDeliveryDateAndDeliveryStatusNot(Long userId, LocalDate deliveryDate, String deliveryStatus);

}