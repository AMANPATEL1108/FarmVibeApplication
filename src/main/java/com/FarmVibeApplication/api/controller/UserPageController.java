package com.FarmVibeApplication.api.controller;

import com.FarmVibeApplication.api.Repository.AddressRepository;
import com.FarmVibeApplication.api.Repository.OrderRepository;
import com.FarmVibeApplication.api.Repository.ProductRepository;
import com.FarmVibeApplication.api.Repository.UserRepository;
import com.FarmVibeApplication.api.model.Address;
import com.FarmVibeApplication.api.model.Order;
import com.FarmVibeApplication.api.model.ProductDetails;
import com.FarmVibeApplication.api.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class UserPageController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("contentPage", "userPages/home");
        return "usermaster";
    }

    @GetMapping("/pageUrl")
    public String changeContent(@RequestParam("page") String page,
                                @RequestParam(value = "id", required = false) Long id,
                                @RequestParam(value = "productId", required = false) Long productId,
                                @RequestParam(value = "qty", required = false) Integer qty,
                                @RequestParam(value = "orderDate", required = false) String orderDate,
                                @RequestParam(value = "deliveryDate", required = false) String deliveryDate,
                                @RequestParam(value = "userId", required = false) Long userId,
                                @RequestParam(value = "orderStatus", required = false) String orderStatus,
                                Model model) {

        if ("products".equals(page)) {
            model.addAttribute("products", productRepository.findAll());
        }

        if ("productdetails".equals(page)) {
            if (id == null) return "redirect:/pageUrl?page=products";
            Optional<ProductDetails> productOpt = productRepository.findById(id);
            if (productOpt.isEmpty()) return "redirect:/pageUrl?page=products";
            model.addAttribute("product", productOpt.get());
        }

        if ("addressdetails".equals(page)) {
            if (productId == null || qty == null || orderDate == null || deliveryDate == null || userId == null) {
                return "redirect:/pageUrl?page=products";
            }
            Optional<ProductDetails> productOpt = productRepository.findById(productId);
            Optional<User> userOpt = userRepository.findById(userId);

            if (productOpt.isEmpty() || userOpt.isEmpty()) {
                return "redirect:/pageUrl?page=products";
            }

            ProductDetails product = productOpt.get();
            User user = userOpt.get();

            model.addAttribute("product", product);
            model.addAttribute("qty", qty);
            model.addAttribute("orderDate", LocalDate.parse(orderDate));
            model.addAttribute("deliveryDate", LocalDate.parse(deliveryDate));
            model.addAttribute("userId", user.getUserId());
            System.out.println("User id 1 is "+user.getUserId());
            model.addAttribute("addresses", addressRepository.findByUser_userId(user.getUserId()));
            model.addAttribute("newAddress", new Address());
        }

        if ("order-detail".equals(page)) {
            if (userId == null) {
                System.out.println("❌ userId is null. Redirecting to home.");
                return "redirect:/";
            }

            String statusToFilter = (orderStatus != null && !orderStatus.isEmpty()) ? orderStatus : "Not Delivered";

            // Filter orders by delivery status and userId
            List<Order> filteredOrders = orderRepository.findByDeliveryStatus(statusToFilter).stream()
                    .filter(order -> order.getUser().getUserId().equals(userId))
                    .collect(Collectors.toList());

            // Group orders by delivery date
            Map<LocalDate, List<Order>> groupedOrders = filteredOrders.stream()
                    .collect(Collectors.groupingBy(Order::getDeliveryDate));

            // Add attributes to model
            model.addAttribute("groupedOrders", groupedOrders);
            model.addAttribute("selectedStatus", statusToFilter);
            model.addAttribute("userId", userId); // ✅ Ensures it's available in Thymeleaf

            System.out.println("✅ userId found and passed to view: " + userId);
        }

        if ("order-history-old".equals(page)) {
            if (userId == null) return "redirect:/";

            String statusToFilter = (orderStatus != null && !orderStatus.isEmpty()) ? orderStatus : "Delivered";

            List<Order> filteredOrders = orderRepository.findByDeliveryStatus(statusToFilter).stream()
                    .filter(order -> order.getUser().getUserId().equals(userId))
                    .collect(Collectors.toList());

            Map<LocalDate, List<Order>> groupedOrders = filteredOrders.stream()
                    .collect(Collectors.groupingBy(Order::getDeliveryDate));

            model.addAttribute("groupedOrders", groupedOrders);
            model.addAttribute("selectedStatus", statusToFilter);
            model.addAttribute("userId", userId);
        }

        model.addAttribute("contentPage", "userPages/" + page);
        return "usermaster";
    }
}