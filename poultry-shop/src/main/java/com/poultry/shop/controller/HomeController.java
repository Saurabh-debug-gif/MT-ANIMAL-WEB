package com.poultry.shop.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Authentication authentication) {

        // Not logged in
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        // Logged in → check role
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                return "redirect:/admin/products";
            }
        }

        // Default USER
        return "redirect:/products";
    }
}
