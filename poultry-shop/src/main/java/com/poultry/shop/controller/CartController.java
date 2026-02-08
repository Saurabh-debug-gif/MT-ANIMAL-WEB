package com.poultry.shop.controller;

import com.poultry.shop.model.CartItem;
import com.poultry.shop.model.Product;
import com.poultry.shop.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final ProductService productService;

    public CartController(ProductService productService) {
        this.productService = productService;
    }

    /* ================= VIEW CART ================= */

    @GetMapping
    public String viewCart(HttpSession session, Model model) {

        List<CartItem> cart = getCart(session);

        // ✅ Subtotal (without GST)
        double subTotal = cart.stream()
                .mapToDouble(item ->
                        item.getProduct().getPrice() * item.getQuantity()
                )
                .sum();

        // ✅ Total GST
        double totalGst = cart.stream()
                .mapToDouble(item ->
                        item.getGstAmount() * item.getQuantity()
                )
                .sum();

        double finalTotal = subTotal + totalGst;

        model.addAttribute("cart", cart);
        model.addAttribute("subTotal", subTotal);
        model.addAttribute("totalGst", totalGst);
        model.addAttribute("finalTotal", finalTotal);

        return "cart";
    }

    /* ================= ADD PRODUCT (fallback) ================= */

    @PostMapping("/add/{id}")
    public String addToCart(@PathVariable Long id, HttpSession session) {

        Product product = productService.getById(id);
        if (product == null) return "redirect:/products";

        List<CartItem> cart = getCart(session);

        for (CartItem item : cart) {
            if (item.getProduct().getId().equals(id)) {
                item.setQuantity(item.getQuantity() + 1);
                return "redirect:/products";
            }
        }

        cart.add(new CartItem(product, 1));
        return "redirect:/products";
    }

    /* ================= INCREASE QTY (AJAX) ================= */

    @PostMapping("/increase/{id}")
    @ResponseBody
    public String increase(@PathVariable Long id, HttpSession session) {

        List<CartItem> cart = getCart(session);

        for (CartItem item : cart) {
            if (item.getProduct().getId().equals(id)) {
                item.setQuantity(item.getQuantity() + 1);
                session.setAttribute("cart", cart);  // 🔥 persist in session
                return String.valueOf(item.getQuantity());  // 🔥 return qty
            }
        }

        Product product = productService.getById(id);
        cart.add(new CartItem(product, 1));
        session.setAttribute("cart", cart);

        return "1";
    }


    /* ================= DECREASE QTY (AJAX) ================= */

    @PostMapping("/decrease/{id}")
    @ResponseBody
    public String decrease(@PathVariable Long id, HttpSession session) {

        List<CartItem> cart = getCart(session);

        for (int i = 0; i < cart.size(); i++) {
            CartItem item = cart.get(i);

            if (item.getProduct().getId().equals(id)) {

                int newQty = item.getQuantity() - 1;

                if (newQty <= 0) {
                    cart.remove(i);
                    session.setAttribute("cart", cart);
                    return "0";
                }

                item.setQuantity(newQty);
                session.setAttribute("cart", cart);
                return String.valueOf(newQty);
            }
        }

        return "0";
    }


    /* ================= GET QTY (AJAX helper) ================= */

    @GetMapping("/qty/{id}")
    @ResponseBody
    public int getQty(@PathVariable Long id, HttpSession session) {

        for (CartItem item : getCart(session)) {
            if (item.getProduct().getId().equals(id)) {
                return item.getQuantity();
            }
        }
        return 0;
    }

    /* ================= REMOVE ITEM ================= */

    @GetMapping("/remove/{id}")
    public String removeFromCart(@PathVariable Long id, HttpSession session) {
        getCart(session).removeIf(item ->
                item.getProduct().getId().equals(id)
        );
        return "redirect:/cart";
    }

    /* ================= SESSION CART ================= */

    @SuppressWarnings("unchecked")
    private List<CartItem> getCart(HttpSession session) {

        Object obj = session.getAttribute("cart");

        if (obj == null) {
            List<CartItem> newCart = new ArrayList<>();
            session.setAttribute("cart", newCart);
            return newCart;
        }

        return (List<CartItem>) obj;
    }
}
