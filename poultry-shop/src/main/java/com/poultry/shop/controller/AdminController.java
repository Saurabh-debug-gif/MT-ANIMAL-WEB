package com.poultry.shop.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.poultry.shop.model.Product;
import com.poultry.shop.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductService productService;
    private final Cloudinary cloudinary;

    // Constructor injection for both services
    public AdminController(ProductService productService, Cloudinary cloudinary) {
        this.productService = productService;
        this.cloudinary = cloudinary;
    }

    // ─────────────────────────────────────────────
    // Helper: Upload a MultipartFile to Cloudinary
    // Returns the secure URL string
    // ─────────────────────────────────────────────
    private String uploadToCloudinary(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "poultry_shop/products",   // organizes files in Cloudinary dashboard
                        "resource_type", "image"
                )
        );
        return (String) uploadResult.get("secure_url");  // permanent HTTPS URL
    }

    // 1️⃣ PRODUCT LIST PAGE
    @GetMapping("/products")
    public String adminProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "admin-products";
    }

    // 2️⃣ ADD PRODUCT FORM
    @GetMapping("/products/new")
    public String addProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "admin-add-product";
    }

    // 3️⃣ SAVE PRODUCT (Add or Edit)
    @PostMapping("/products/save")
    public String saveProduct(
            @ModelAttribute Product product,
            @RequestParam("image") MultipartFile image,
            @RequestParam("knowMoreImage") MultipartFile knowMoreImage
    ) {
        try {
            // Fetch existing product if we are editing (id is present)
            Product existing = null;
            if (product.getId() != null) {
                existing = productService.getById(product.getId());
            }

            // ✅ Handle main product image
            if (!image.isEmpty()) {
                // New image uploaded → send to Cloudinary
                String imageUrl = uploadToCloudinary(image);
                product.setImageUrl(imageUrl);
            } else if (existing != null) {
                // No new image → keep the old Cloudinary URL
                product.setImageUrl(existing.getImageUrl());
            }

            // ✅ Handle "Know More" image
            if (!knowMoreImage.isEmpty()) {
                // New image uploaded → send to Cloudinary
                String knowMoreUrl = uploadToCloudinary(knowMoreImage);
                product.setKnowMoreImageUrl(knowMoreUrl);
            } else if (existing != null) {
                // No new image → keep the old Cloudinary URL
                product.setKnowMoreImageUrl(existing.getKnowMoreImageUrl());
            }

            product.setActive(true);
            productService.save(product);

        } catch (Exception e) {
            e.printStackTrace();
            // Optional: you can add a flash error message here using RedirectAttributes
        }

        return "redirect:/admin/products";
    }

    // 4️⃣ EDIT PRODUCT - Load existing product into form
    @GetMapping("/products/edit/{id}")
    public String editProduct(@PathVariable Long id, Model model) {
        Product product = productService.getById(id);
        model.addAttribute("product", product);
        return "admin-add-product";
    }

    // 5️⃣ DELETE PRODUCT
    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteById(id);
        return "redirect:/admin/products";
    }
}