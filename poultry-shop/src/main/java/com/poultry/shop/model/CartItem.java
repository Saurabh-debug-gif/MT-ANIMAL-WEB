package com.poultry.shop.model;

public class CartItem {

    private Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /* ================= PRICE LOGIC ================= */

    // Price WITHOUT GST (per qty)
    public double getTotalPrice() {
        return product.getPrice() * quantity;
    }

    // GST amount for ONE unit
    public double getGstAmount() {
        return (product.getPrice() * product.getGstPercent()) / 100.0;
    }

    // Price WITH GST for ONE unit
    public double getFinalPriceWithGst() {
        return product.getPrice() + getGstAmount();
    }

    // Final price WITH GST × quantity
    public double getTotalPriceWithGst() {
        return getFinalPriceWithGst() * quantity;
    }
}


