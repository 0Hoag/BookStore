package com.example.identityservice.repository;

import com.example.identityservice.entity.Orders;
import com.example.identityservice.entity.SelectedProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SelectedProductRepository extends JpaRepository<SelectedProduct, String> {
}
