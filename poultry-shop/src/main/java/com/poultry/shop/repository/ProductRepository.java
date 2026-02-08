package com.poultry.shop.repository;

import com.poultry.shop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByActiveTrue();

    List<Product> findByActiveTrueAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String name,
            String description
    );
}
