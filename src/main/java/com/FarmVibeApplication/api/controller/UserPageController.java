package com.FarmVibeApplication.api.controller;

import com.FarmVibeApplication.api.Repository.ProductRepository;
import com.FarmVibeApplication.api.model.ProductDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class UserPageController {

    @Autowired
    private ProductRepository productRepository;

    // Home page loads with usermaster and contentPage=userPages/home.html
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("contentPage", "userPages/home");
        return "usermaster";
    }

    // Dynamic page loading via 'page' param: products, productdetails, etc.
    @GetMapping("/pageUrl")
    public String changeContent(@RequestParam("page") String page,
                                @RequestParam(value = "id", required = false) Long id,
                                Model model) {

        if ("products".equals(page)) {
            model.addAttribute("products", productRepository.findAll());
        }

        if ("productdetails".equals(page)) {
            if (id == null) {
                // if no id given, redirect to products page or show error
                return "redirect:/pageUrl?page=products";
            }
            Optional<ProductDetails> productOpt = productRepository.findById(id);
            if (productOpt.isPresent()) {
                model.addAttribute("product", productOpt.get());
            } else {
                // product not found, redirect to products page
                return "redirect:/pageUrl?page=products";
            }
        }

        model.addAttribute("contentPage", "userPages/" + page);
        return "usermaster";
    }
}
