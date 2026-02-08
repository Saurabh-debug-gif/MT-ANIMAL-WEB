package com.poultry.shop.model;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===== BASIC INFO =====

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    // ===== PRICING =====

    // MRP without GST
    @Column(nullable = false)
    private double price;

    // GST percentage (5, 12, 18 etc.)
    @Column(nullable = false)
    private int gstPercent;

    // ===== INVENTORY =====

    @Column(nullable = false)
    private int stock;

    @Column(nullable = false)
    private boolean active = true;

    // ===== IMAGES =====

    // Main product image (shown on product card)
    @Column(length = 500)
    private String imageUrl;

    // Image shown when user clicks "Know More"
    @Column(length = 500)
    private String knowMoreImageUrl;

    // ===== GETTERS & SETTERS =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getGstPercent() {
        return gstPercent;
    }

    public void setGstPercent(int gstPercent) {
        this.gstPercent = gstPercent;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getKnowMoreImageUrl() {
        return knowMoreImageUrl;
    }

    public void setKnowMoreImageUrl(String knowMoreImageUrl) {
        this.knowMoreImageUrl = knowMoreImageUrl;
    }
}
