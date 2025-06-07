package com.FarmVibeApplication.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserPageController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("contentPage", "userPages/home");
        return "usermaster";
    }

    @GetMapping("/pageUrl")
    public String changeContent(@RequestParam("page") String page, Model model) {
        model.addAttribute("contentPage", "userPages/" + page); // Updated path
        return "usermaster";
    }
}