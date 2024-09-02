package com.example.identityservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.identityservice.entity.Orders;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, String> {}
