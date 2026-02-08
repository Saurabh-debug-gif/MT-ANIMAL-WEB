package com.poultry.shop.service;

import com.poultry.shop.model.Product;
import com.poultry.shop.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    // 1 Constructor-based Dependency Injection
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // ⃣ Get ALL products (Admin side - includes inactive products)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Get ONLY ACTIVE products (User side - visible on website)
    public List<Product> getAllActiveProducts() {
        return productRepository.findByActiveTrue();
    }

    // 4Save or Update product
    public Product save(Product product) {
        return productRepository.save(product);
    }

    //Get product by ID
    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    //  Delete product by ID
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> search(String keyword) {
        return productRepository
                .findByActiveTrueAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword);
    }

}

