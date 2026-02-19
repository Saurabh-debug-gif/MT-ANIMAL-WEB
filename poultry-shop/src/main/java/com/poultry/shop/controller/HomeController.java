package com.poultry.shop.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Authentication authentication) {

        // 🌍 If not logged in → show public homepage (or products page)
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/products";   // or return "index" if you have a homepage template
        }

        // 🔐 Logged in → Admin goes to admin panel
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                return "redirect:/admin/products";
            }
        }

        // 👤 Logged in normal user
        return "redirect:/products";
    }
}
