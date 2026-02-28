package com.poultry.shop.controller;

import com.poultry.shop.model.CartItem;
import com.poultry.shop.model.Product;
import com.poultry.shop.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // ================= SHOW PRODUCTS =================
    @GetMapping("/products")
    public String showProducts(Model model, HttpSession session, Authentication authentication) {

        List<Product> products = productService.getAllActiveProducts();
        model.addAttribute("products", products);

        addCartInfoToModel(model, session);
        addAuthInfoToModel(model, authentication);

        return "products";
    }

    // ================= SEARCH PRODUCTS =================
    @GetMapping("/products/search")
    public String searchProducts(@RequestParam("q") String query,
                                 Model model,
                                 HttpSession session,
                                 Authentication authentication) {

        List<Product> products = productService.search(query);
        model.addAttribute("products", products);
        model.addAttribute("searchQuery", query);

        addCartInfoToModel(model, session);
        addAuthInfoToModel(model, authentication);

        return "products";
    }

    // ================= AUTH INFO =================
    private void addAuthInfoToModel(Model model, Authentication authentication) {
        boolean isLoggedIn = authentication != null
                && authentication.isAuthenticated()
                && !authentication.getName().equals("anonymousUser");

        model.addAttribute("isLoggedIn", isLoggedIn);
        model.addAttribute("userName", isLoggedIn ? authentication.getName() : "");
    }

    // ================= COMMON CART LOGIC =================
    private void addCartInfoToModel(Model model, HttpSession session) {

        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

        Map<Long, Integer> cartQtyMap = new HashMap<>();
        int cartCount = 0;

        if (cart != null) {
            for (CartItem item : cart) {
                cartQtyMap.put(item.getProduct().getId(), item.getQuantity());
                cartCount += item.getQuantity();
            }
        }

        model.addAttribute("cart", cartQtyMap);
        model.addAttribute("cartCount", cartCount);
    }
}