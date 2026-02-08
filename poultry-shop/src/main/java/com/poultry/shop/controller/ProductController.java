package com.poultry.shop.controller;

import com.poultry.shop.model.CartItem;
import com.poultry.shop.model.Product;
import com.poultry.shop.service.ProductService;
import jakarta.servlet.http.HttpSession;
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
    public String showProducts(Model model, HttpSession session) {

        List<Product> products = productService.getAllActiveProducts();
        model.addAttribute("products", products);

        addCartInfoToModel(model, session);

        return "products";
    }

    // ================= SEARCH PRODUCTS =================
    @GetMapping("/products/search")
    public String searchProducts(@RequestParam("q") String query,
                                 Model model,
                                 HttpSession session) {

        List<Product> products = productService.search(query);
        model.addAttribute("products", products);
        model.addAttribute("searchQuery", query);

        addCartInfoToModel(model, session);

        return "products";
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
                cartCount += item.getQuantity();  // ✅ total qty, not item count
            }
        }

        model.addAttribute("cart", cartQtyMap);
        model.addAttribute("cartCount", cartCount);
    }
}
