package com.FarmVibeApplication.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class AdminPageController {


    @GetMapping
    public String home(Model model) {
        model.addAttribute("contentPage", "adminPages/asigning");
        return "adminmaster";
    }

    @GetMapping("/pageUrl")
    public String changeContent(@RequestParam("page") String page, Model model) {
        model.addAttribute("contentPage", "adminPages/" + page);
        return "adminmaster";
    }



}