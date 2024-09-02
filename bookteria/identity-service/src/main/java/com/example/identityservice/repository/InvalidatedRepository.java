package com.example.identityservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.identityservice.entity.InvalidatedToken;

@Repository
public interface InvalidatedRepository extends JpaRepository<InvalidatedToken, String> {}
