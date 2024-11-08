package com.example.payment_service.repository;

import java.util.Set;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.payment_service.entity.Payment;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {
    Set<Payment> findByUserId(String userId);
}
