package com.FarmVibeApplication.api.controller;

import com.FarmVibeApplication.api.Repository.OrderRepository;
import com.FarmVibeApplication.api.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class OrderDetailsController {

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/order-details")
    public String showOrderDetails(@RequestParam("orderStatus") String orderStatus,
                                   @RequestParam("userId") Long userId,
                                   Model model) {
        List<Order> userOrders = orderRepository.findByDeliveryStatus(orderStatus)
                .stream()
                .filter(order -> order.getUser().getUserId().equals(userId))
                .collect(Collectors.toList());

        Map<LocalDate, List<Order>> groupedOrders = userOrders.stream()
                .collect(Collectors.groupingBy(Order::getDeliveryDate));

        model.addAttribute("groupedOrders", groupedOrders);
        model.addAttribute("userId", userId);
        return "order-detail";
    }
}