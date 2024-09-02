package com.example.payment_service.repository;

import com.example.payment_service.entity.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;


@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {
    Set<Payment> findByUserId(String userId);
}
