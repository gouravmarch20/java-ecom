package com.example.FakeCommerce.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.FakeCommerce.schema.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByCategory_Name(String categoryName);

    @Query(nativeQuery = true, value = "SELECT DISTINCT c.name FROM categories c JOIN products p ON p.category_id = c.id")
    List<String> getAllLinkedCategories();

    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE p.id = :id")
    java.util.Optional<Product> findProductWithDetailsById(Long id);
}
