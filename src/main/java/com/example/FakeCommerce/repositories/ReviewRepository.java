package com.example.FakeCommerce.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.FakeCommerce.schema.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // order and product are LAZY, so fetch them with the review to build the response outside a session
    @Query("SELECT r FROM Review r JOIN FETCH r.order JOIN FETCH r.product")
    List<Review> findAllWithDetails();

    @Query("SELECT r FROM Review r JOIN FETCH r.order JOIN FETCH r.product WHERE r.id = :id")
    Optional<Review> findByIdWithDetails(Long id);

    @Query("SELECT r FROM Review r JOIN FETCH r.order JOIN FETCH r.product WHERE r.product.id = :productId")
    List<Review> findByProductId(Long productId);

    @Query("SELECT r FROM Review r JOIN FETCH r.order JOIN FETCH r.product WHERE r.order.id = :orderId")
    List<Review> findByOrderId(Long orderId);

    boolean existsByOrderIdAndProductIdAndUserId(Long orderId, Long productId, String userId);
}
