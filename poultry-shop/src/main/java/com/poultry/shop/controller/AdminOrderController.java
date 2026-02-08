package com.poultry.shop.controller;

import com.poultry.shop.model.Order;
import com.poultry.shop.repository.OrderRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminOrderController {

    private final OrderRepository orderRepository;

    public AdminOrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping("/orders")
    public String viewOrders(Model model) {
        model.addAttribute("orders", orderRepository.findAll());
        return "admin-orders";
    }
    @GetMapping("/orders/update/{id}")
    public String updateOrderForm(@PathVariable Long id, Model model) {
        Order order = orderRepository.findById(id).orElseThrow();
        model.addAttribute("order", order);
        return "admin-update-order";
    }

    @PostMapping("/orders/update")
    public String updateOrder(
            @RequestParam Long id,
            @RequestParam String status,
            @RequestParam(required = false) String trackingId,
            @RequestParam(required = false) String adminMessage
    ) {
        Order order = orderRepository.findById(id).orElseThrow();
        order.setStatus(status);
        order.setTrackingId(trackingId);
        order.setAdminMessage(adminMessage);

        orderRepository.save(order);
        return "redirect:/admin/orders";
    }
    @GetMapping("/order-status/{id}")
    public String orderStatus(@PathVariable Long id, Model model) {
        Order order = orderRepository.findById(id).orElseThrow();
        model.addAttribute("order", order);
        return "order-status";
    }


}

