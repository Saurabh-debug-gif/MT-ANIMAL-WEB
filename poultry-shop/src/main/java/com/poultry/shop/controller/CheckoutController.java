package com.poultry.shop.controller;

import com.poultry.shop.model.*;
import com.poultry.shop.repository.OrderRepository;
import com.poultry.shop.service.PaymentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class CheckoutController {

    private final OrderRepository orderRepository;
    private final PaymentService paymentService;

    public CheckoutController(OrderRepository orderRepository,
                              PaymentService paymentService) {
        this.orderRepository = orderRepository;
        this.paymentService = paymentService;
    }

    /* ================= CHECKOUT PAGE ================= */

    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {

        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

        if (cart == null || cart.isEmpty()) {
            return "redirect:/cart";
        }

        double total = cart.stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();

        model.addAttribute("cart", cart);
        model.addAttribute("total", total);

        return "checkout";
    }

    /* ================= PLACE ORDER (CREATE CASHFREE ORDER ONLY) ================= */

    @PostMapping("/checkout/place-order")
    public String placeOrder(
            @RequestParam String name,
            @RequestParam String phone,
            @RequestParam String email,
            @RequestParam String address,
            @RequestParam String city,
            @RequestParam String state,
            @RequestParam String pincode,
            HttpSession session
    ) {

        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) return "redirect:/cart";

        double total = cart.stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();

        // Create a temporary tracking id for payment reference
        String trackingId = "ORD_" + UUID.randomUUID();

        try {
            // 💳 Create Cashfree order (NO DB SAVE YET)
            String paymentSessionId = paymentService.createPaymentSession(
                    trackingId,
                    total,
                    name,
                    phone,
                    email
            );


            // Store checkout details in session until payment success
            session.setAttribute("checkout_name", name);
            session.setAttribute("checkout_phone", phone);
            session.setAttribute("checkout_email", email);
            session.setAttribute("checkout_address", address);
            session.setAttribute("checkout_city", city);
            session.setAttribute("checkout_state", state);
            session.setAttribute("checkout_pincode", pincode);
            session.setAttribute("checkout_cart", cart);
            session.setAttribute("checkout_trackingId", trackingId);

            return "redirect:/pay?sessionId=" + paymentSessionId;

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/checkout?error=payment_failed";
        }
    }

    /* ================= PAYMENT PAGE ================= */

    @GetMapping("/pay")
    public String payPage(@RequestParam String sessionId, Model model) {
        model.addAttribute("sessionId", sessionId);
        return "pay"; // create pay.html
    }

    /* ================= PAYMENT SUCCESS ================= */

    @GetMapping("/payment/success")
    public String paymentSuccess(
            @RequestParam(name = "order_id", required = false) String cashfreeOrderId,
            HttpSession session
    ) {

        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("checkout_cart");

        if (cart == null || cart.isEmpty()) {
            return "redirect:/products";
        }

        Order order = new Order();
        order.setCustomerName((String) session.getAttribute("checkout_name"));
        order.setPhone((String) session.getAttribute("checkout_phone"));
        order.setEmail((String) session.getAttribute("checkout_email"));
        order.setAddress((String) session.getAttribute("checkout_address"));
        order.setCity((String) session.getAttribute("checkout_city"));
        order.setState((String) session.getAttribute("checkout_state"));
        order.setPincode((String) session.getAttribute("checkout_pincode"));
        order.setTrackingId((String) session.getAttribute("checkout_trackingId"));
        order.setPaymentStatus("PAID");
        order.setStatus("PLACED");

        double total = 0;
        List<OrderItem> items = new ArrayList<>();

        for (CartItem ci : cart) {
            OrderItem item = new OrderItem();
            item.setProductName(ci.getProduct().getName());
            item.setPrice(ci.getProduct().getPrice());
            item.setQuantity(ci.getQuantity());
            item.setOrder(order);

            total += ci.getProduct().getPrice() * ci.getQuantity();
            items.add(item);
        }

        order.setItems(items);
        order.setTotalAmount(total);

        orderRepository.save(order);

        // Clear session
        session.removeAttribute("cart");
        session.removeAttribute("checkout_cart");

        return "redirect:/order-status/" + order.getId();


    }

    /* ================= PAYMENT FAILURE ================= */

    @GetMapping("/payment/failure")
    public String paymentFailure() {
        return "redirect:/checkout?error=payment_failed";
    }

    /* ================= WEBHOOK ================= */

    @PostMapping("/payment/webhook")
    @ResponseBody
    public String paymentWebhook(@RequestBody String payload) {
        System.out.println("Cashfree Webhook: " + payload);
        return "OK";
    }
    @GetMapping("/order-success")
    public String orderSuccess() {
        return "order-success";  // maps to order-success.html
    }
    @GetMapping("/order-status/{orderId}")
    public String orderStatus(@PathVariable Long orderId, Model model) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        model.addAttribute("order", order);
        return "order-status";
    }

    @GetMapping("/my-orders")
    public String myOrders(@AuthenticationPrincipal(expression = "attributes['email']") String email,
                           Model model) {

        if (email == null) {
            return "redirect:/login";
        }

        List<Order> orders = orderRepository.findByEmailOrderByIdDesc(email);
        model.addAttribute("orders", orders);

        return "my-orders";   // my-orders.html
    }

}
