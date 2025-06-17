package com.FarmVibeApplication.api.controller;

import com.FarmVibeApplication.api.Repository.AddressRepository;
import com.FarmVibeApplication.api.Repository.CategoryRepository;
import com.FarmVibeApplication.api.Repository.ProductRepository;
import com.FarmVibeApplication.api.Repository.UserRepository;
import com.FarmVibeApplication.api.model.*;
import com.FarmVibeApplication.api.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private ProductRepository prodRepo;

    @Autowired
    private AddressRepository addrRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private OrderService orderService;

    @GetMapping("/address")
    public String showAddress(@RequestParam Long productId,
                              @RequestParam int qty,
                              @RequestParam String orderDate,
                              @RequestParam String deliveryDate,
                              @RequestParam Long userId,
                              Model model) {

        ProductDetails pd = prodRepo.findById(productId).orElse(null);
        User user = userRepo.findById(userId).orElse(null);
        List<Address> userAddresses = addrRepo.findByUser_userId(userId);

        model.addAttribute("product", pd);
        model.addAttribute("qty", qty);
        model.addAttribute("orderDate", LocalDate.parse(orderDate));
        model.addAttribute("deliveryDate", LocalDate.parse(deliveryDate));
        model.addAttribute("addresses", userAddresses);
        model.addAttribute("newAddress", new Address());
        model.addAttribute("userId", userId);

        model.addAttribute("contentPage", "userPages/addressdetails");
        return "usermaster";
    }

    @PostMapping("/address")
    public String handleAddress(@ModelAttribute("newAddress") Address newAddr,
                                @RequestParam(required = false) Long selectedAddressId,
                                @RequestParam(required = false) String city,
                                @RequestParam(required = false) String area,
                                @RequestParam Long productId,
                                @RequestParam int qty,
                                @RequestParam String orderDate,
                                @RequestParam String deliveryDate,
                                @RequestParam Long userId) {
        User user = userRepo.findById(userId).orElse(null);

        if (selectedAddressId != null) {
            return "redirect:/order/payment?productId=" + productId +
                    "&qty=" + qty +
                    "&orderDate=" + orderDate +
                    "&deliveryDate=" + deliveryDate +
                    "&addressId=" + selectedAddressId +
                    "&userId=" + userId;
        } else {
            newAddr.setUser(user);
            newAddr.setCity(city);
            newAddr.setArea(area);
            addrRepo.save(newAddr);

            return "redirect:/order/address?productId=" + productId +
                    "&qty=" + qty +
                    "&orderDate=" + orderDate +
                    "&deliveryDate=" + deliveryDate +
                    "&userId=" + userId;
        }
    }


    @GetMapping("/payment")
    public String showPayment(@RequestParam Long productId,
                              @RequestParam int qty,
                              @RequestParam String orderDate,
                              @RequestParam String deliveryDate,
                              @RequestParam Long addressId,
                              @RequestParam Long userId,
                              Model model) {

        model.addAttribute("product", prodRepo.findById(productId).orElse(null));
        model.addAttribute("qty", qty);
        model.addAttribute("orderDate", LocalDate.parse(orderDate));
        model.addAttribute("deliveryDate", LocalDate.parse(deliveryDate));
        model.addAttribute("address", addrRepo.findById(addressId).orElse(null));
        model.addAttribute("userId", userId);

        model.addAttribute("contentPage", "userPages/payment");
        return "usermaster";
    }

    @PostMapping("/confirm")
    public String showConfirm(@RequestParam Long productId,
                              @RequestParam int qty,
                              @RequestParam String orderDate,
                              @RequestParam String deliveryDate,
                              @RequestParam Long addressId,
                              @RequestParam Long userId,
                              Model model) {

        ProductDetails pd = prodRepo.findById(productId).orElse(null);
        Address addr = addrRepo.findById(addressId).orElse(null);
        User user = userRepo.findById(userId).orElse(null);
        double total = pd.getPrice() * qty;

        model.addAttribute("product", pd);
        model.addAttribute("qty", qty);
        model.addAttribute("orderDate", LocalDate.parse(orderDate));
        model.addAttribute("deliveryDate", LocalDate.parse(deliveryDate));
        model.addAttribute("address", addr);
        model.addAttribute("user", user);
        model.addAttribute("category", pd.getCategory());
        model.addAttribute("total", total);
        model.addAttribute("userId", userId);

        model.addAttribute("contentPage", "userPages/confirmation");
        return "usermaster";
    }

    @PostMapping("/place")
    public String placeOrder(@RequestParam Long productId,
                             @RequestParam int qty,
                             @RequestParam String orderDate,
                             @RequestParam String deliveryDate,
                             @RequestParam Long addressId,
                             @RequestParam Long userId,
                             Model model) {

        ProductDetails pd = prodRepo.findById(productId).orElse(null);
        Address addr = addrRepo.findById(addressId).orElse(null);
        User user = userRepo.findById(userId).orElse(null);

        if (pd == null || addr == null || user == null) {
            return "redirect:/error";
        }

        int currentStock = pd.getStock();

        if (qty > currentStock) {
            model.addAttribute("error", "Requested quantity exceeds available stock.");
            return "redirect:/products";
        }

        pd.setStock(currentStock - qty);
        prodRepo.save(pd);

        Order o = Order.builder()
                .product(pd)
                .category(pd.getCategory())
                .address(addr)
                .user(user)
                .quantity(qty)
                .orderDate(LocalDate.parse(orderDate))
                .deliveryDate(LocalDate.parse(deliveryDate))
                .totalPrice(pd.getPrice() * qty)
                .paymentMethod("Cash on Delivery")
                .paymentStatus("Pending")
                .deliveryStatus("Not Delivered")
                .orderConfirmed(true)
                .build();

        orderService.save(o);

        return "redirect:/";
    }
}
