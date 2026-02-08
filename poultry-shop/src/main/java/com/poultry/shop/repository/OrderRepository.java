package com.poultry.shop.repository;

import com.poultry.shop.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByPaymentStatus(String status);
    List<Order> findByEmailOrderByIdDesc(String email);
}
