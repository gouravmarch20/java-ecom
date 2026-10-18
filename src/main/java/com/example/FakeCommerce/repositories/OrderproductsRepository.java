package com.example.FakeCommerce.repositories;

import  java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import com.example.FakeCommerce.schema.Order;
import com.example.FakeCommerce.schema.OrderProducts;


public interface OrderproductsRepository extends JpaRepository<OrderProducts , Long> {
    @Query("SELECT op FROM OrderProducts op JOIN FETCH op.product WHERE op.order.id = :orderId")
    List<OrderProducts> findByOrderId(Long orderId);

    @Query("SELECT op FROM OrderProducts op JOIN FETCH op.product JOIN FETCH op.order WHERE op.order.id IN :orderIds")
    List<OrderProducts> findByOrderIdIn(List<Long> orderIds);

    @Query("SELECT op FROM OrderProducts op JOIN FETCH op.product WHERE op.order = :order")
    List<OrderProducts> findOrderWithProduct(Order order);

    boolean existsByOrderIdAndProductId(Long orderId, Long productId);
}
