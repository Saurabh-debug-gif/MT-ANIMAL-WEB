package com.poultry.shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminAuthController {

    @GetMapping("/login")
    public String loginPage() {
        return "admin-login";
    }
}
