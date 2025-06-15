package com.FarmVibeApplication.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WellKnownController {

    @RequestMapping("/.well-known/**")
    @ResponseBody
    public ResponseEntity<String> handleWellKnown() {
        return ResponseEntity.notFound().build();  // Optional: return empty or dummy response
    }
}
