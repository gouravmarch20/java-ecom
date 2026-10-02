package com.example.FakeCommerce.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.FakeCommerce.schema.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    
}