package com.example.identityservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.identityservice.entity.SelectedProduct;

@Repository
public interface SelectedProductRepository extends JpaRepository<SelectedProduct, String> {}
