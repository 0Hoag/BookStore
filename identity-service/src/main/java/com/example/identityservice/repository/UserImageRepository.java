package com.example.identityservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.identityservice.entity.UserImage;

@Repository
public interface UserImageRepository extends JpaRepository<UserImage, String> {}
