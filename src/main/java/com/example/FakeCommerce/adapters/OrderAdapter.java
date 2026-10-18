package com.example.FakeCommerce.adapters;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.FakeCommerce.dtos.GetOrderResponseDto;
import com.example.FakeCommerce.dtos.OrderItemResponseDto;
import com.example.FakeCommerce.repositories.OrderproductsRepository;
import com.example.FakeCommerce.schema.OrderProducts;

import lombok.RequiredArgsConstructor;
import com.example.FakeCommerce.schema.Order;

@Component 
@RequiredArgsConstructor 
public class OrderAdapter {
    private  final OrderproductsRepository orderproductsRepository;

    public List<GetOrderResponseDto> mapToGetOrderResponseDtos(List<Order> orders) {
        if (orders.isEmpty()) {
            return List.of();
        }
        // one query for the items of all orders, instead of one query per order
        List<Long> orderIds = orders.stream().map(Order::getId).collect(Collectors.toList());
        Map<Long, List<OrderProducts>> itemsByOrderId = orderproductsRepository.findByOrderIdIn(orderIds).stream()
            .collect(Collectors.groupingBy(op -> op.getOrder().getId()));

        return orders.stream()
            .map(order -> buildOrderResponseDto(order, itemsByOrderId.getOrDefault(order.getId(), List.of())))
            .collect(Collectors.toList());
    }
    public  GetOrderResponseDto mapToGetOrderResponseDto(Order order){
        List<OrderProducts> orderProducts = orderproductsRepository.findByOrderId(order.getId());
        return buildOrderResponseDto(order, orderProducts);
    }
    private GetOrderResponseDto buildOrderResponseDto(Order order, List<OrderProducts> orderProducts) {
        List<OrderItemResponseDto> items = mapToOrderItemResponseDto(orderProducts);

               return GetOrderResponseDto.builder()
            .id(order.getId())
            .status(order.getStatus())
            .createdAt(order.getCreatedAt())
            .updatedAt(order.getUpdatedAt())
            .items(items)
            .build();
    }
       public List<OrderItemResponseDto> mapToOrderItemResponseDto(List<OrderProducts> orderProducts) {
        return orderProducts.stream()
            .map(op -> OrderItemResponseDto.builder()
                .productId(op.getProduct().getId())
                .quantity(op.getQuantity())
                .productName(op.getProduct().getTitle())
                .productPrice(op.getProduct().getPrice())
                .productImage(op.getProduct().getImage())
                .subTotal(op.getProduct().getPrice().multiply(BigDecimal.valueOf(op.getQuantity())))
                .build())
            .collect(Collectors.toList());
    }
}
