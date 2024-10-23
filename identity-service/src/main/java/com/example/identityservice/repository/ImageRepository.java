package com.example.identityservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.identityservice.entity.Image;

public interface ImageRepository extends JpaRepository<Image, String> {}
