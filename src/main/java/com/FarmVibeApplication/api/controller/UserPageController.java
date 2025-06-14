package com.FarmVibeApplication.api.controller;

import com.FarmVibeApplication.api.Repository.AddressRepository;
import com.FarmVibeApplication.api.Repository.ProductRepository;
import com.FarmVibeApplication.api.Repository.UserRepository;
import com.FarmVibeApplication.api.model.Address;
import com.FarmVibeApplication.api.model.ProductDetails;
import com.FarmVibeApplication.api.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@Controller
public class UserPageController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

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
            model.addAttribute("addresses", addressRepository.findByUser_userId(user.getUserId()));
            model.addAttribute("newAddress", new Address());
        }

        model.addAttribute("contentPage", "userPages/" + page);
        return "usermaster";
    }
}
