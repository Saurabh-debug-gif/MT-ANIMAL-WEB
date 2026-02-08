package com.poultry.shop.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;
    private String phone;
    private String email;

    private String address;
    private String city;
    private String state;
    private String pincode;

    private double totalAmount;

    private LocalDateTime createdAt = LocalDateTime.now();

    // 🔐 Payment info
    private String paymentId;       // from gateway
    private String paymentStatus;   // PENDING / PAID / FAILED

    // 📦 Order tracking
    private String status;          // PLACED, SHIPPED, OUT_FOR_DELIVERY, DELIVERED
    private String trackingId;      // Courier tracking number

    @Column(length = 1000)
    private String adminMessage;    // Message from admin to customer

    // 🧾 Order Items (ONLY ONCE)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;
}
