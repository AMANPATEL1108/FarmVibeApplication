package com.FarmVibeApplication.api.controller;

import com.FarmVibeApplication.api.Repository.OrderRepository;
import com.FarmVibeApplication.api.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class OrderDetailsController {

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("pageUrl?page=/orders")
    public String showOrderDetails(Model model) {
        // Get all non-delivered orders
        List<Order> nonDeliveredOrders = orderRepository.findByDeliveryStatus("Not Delivered");

        // Group by order date
        Map<LocalDate, List<Order>> groupedOrders = nonDeliveredOrders.stream()
                .collect(Collectors.groupingBy(Order::getOrderDate));

        model.addAttribute("groupedOrders", groupedOrders);
        return "order-detail";
    }
}