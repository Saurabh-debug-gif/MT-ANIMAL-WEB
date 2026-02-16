package com.poultry.shop.controller;

import com.poultry.shop.model.Product;
import com.poultry.shop.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductService productService;

    private static final String UPLOAD_DIR =
            System.getProperty("user.dir") + "/uploads/products/";


    public AdminController(ProductService productService) {
        this.productService = productService;
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
    @PostMapping("/products/save")
    public String saveProduct(
            @ModelAttribute Product product,
            @RequestParam("image") MultipartFile image,
            @RequestParam("knowMoreImage") MultipartFile knowMoreImage
    ) {
        try {
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 👉 Fetch existing product if editing
            Product existing = null;
            if (product.getId() != null) {
                existing = productService.getById(product.getId());
            }

            // ✅ Main product image
            if (!image.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + java.util.UUID.randomUUID() + "_" + image.getOriginalFilename();
                File destination = new File(uploadDir, fileName);
                image.transferTo(destination);
                product.setImageUrl("/uploads/products/" + fileName);
            } else if (existing != null) {
                // 🔥 KEEP OLD IMAGE
                product.setImageUrl(existing.getImageUrl());
            }

            // ✅ Know More image
            if (!knowMoreImage.isEmpty()) {
                String fileName2 = System.currentTimeMillis() + "_knowmore_" + java.util.UUID.randomUUID() + "_" + knowMoreImage.getOriginalFilename();
                File destination2 = new File(uploadDir, fileName2);
                knowMoreImage.transferTo(destination2);
                product.setKnowMoreImageUrl("/uploads/products/" + fileName2);
            } else if (existing != null) {
                // 🔥 KEEP OLD IMAGE
                product.setKnowMoreImageUrl(existing.getKnowMoreImageUrl());
            }

            product.setActive(true);
            productService.save(product);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/admin/products";
    }

    // 4️⃣ EDIT PRODUCT
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
