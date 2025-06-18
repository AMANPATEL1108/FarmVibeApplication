package com.FarmVibeApplication.api.controller;

import com.FarmVibeApplication.api.Repository.CategoryRepository;
import com.FarmVibeApplication.api.Repository.ProductRepository;
import com.FarmVibeApplication.api.model.Category;
import com.FarmVibeApplication.api.model.ProductDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    // ✅ Dedicated URL mapping to avoid clash with UserPageController
    @GetMapping("/category")
    public String showAllCategories(Model model) {
        List<Category> categories = categoryRepository.findAll();
        System.out.println("categories is :"+categories);
        model.addAttribute("categories", categories);
        return "userPages/category";
    }

    // ✅ Show products of selected category
    @GetMapping("/category/products")
    public String showProductsByCategory(@RequestParam("name") String categoryName, Model model) {
        List<ProductDetails> products = productRepository.findByCategory_Name(categoryName);
        model.addAttribute("products", products);
        model.addAttribute("categoryName", categoryName);
        return "userPages/categoryproduct";
    }
}
