package com.example.FakeCommerce.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.FakeCommerce.dtos.CreateReviewRequestDto;
import com.example.FakeCommerce.dtos.GetReviewResponseDto;
import com.example.FakeCommerce.exceptions.BadRequestException;
import com.example.FakeCommerce.exceptions.ResourceNotFoundException;
import com.example.FakeCommerce.repositories.OrderRepository;
import com.example.FakeCommerce.repositories.OrderproductsRepository;
import com.example.FakeCommerce.repositories.ProductRepository;
import com.example.FakeCommerce.repositories.ReviewRepository;
import com.example.FakeCommerce.schema.Order;
import com.example.FakeCommerce.schema.Product;
import com.example.FakeCommerce.schema.Review;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderproductsRepository orderproductsRepository;

    private GetReviewResponseDto toResponseDto(Review review) {
        return GetReviewResponseDto.builder()
                .id(review.getId())
                .orderId(review.getOrder().getId())
                .productId(review.getProduct().getId())
                .productName(review.getProduct().getTitle())
                .userId(review.getUserId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    public List<GetReviewResponseDto> getAllReviews() {
        return reviewRepository.findAllWithDetails().stream()
                .map(this::toResponseDto)
                .toList();
    }

    public GetReviewResponseDto getReviewById(Long id) {
        Review review = reviewRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review with id " + id + " not found"));
        return toResponseDto(review);
    }

    public List<GetReviewResponseDto> getReviewsByProductId(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product with id " + productId + " not found");
        }
        return reviewRepository.findByProductId(productId).stream()
                .map(this::toResponseDto)
                .toList();
    }

    public List<GetReviewResponseDto> getReviewsByOrderId(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException("Order with id " + orderId + " not found");
        }
        return reviewRepository.findByOrderId(orderId).stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Transactional
    public GetReviewResponseDto createReview(CreateReviewRequestDto requestDto) {
        Order order = orderRepository.findById(requestDto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order with id " + requestDto.getOrderId() + " not found"));
        Product product = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + requestDto.getProductId() + " not found"));

        // only products that were bought in this order can be reviewed
        if (!orderproductsRepository.existsByOrderIdAndProductId(order.getId(), product.getId())) {
            throw new BadRequestException("Product " + product.getId() + " is not part of order " + order.getId());
        }
        if (reviewRepository.existsByOrderIdAndProductIdAndUserId(order.getId(), product.getId(), requestDto.getUserId())) {
            throw new BadRequestException("User " + requestDto.getUserId() + " has already reviewed product "
                    + product.getId() + " in order " + order.getId());
        }

        Review review = Review.builder()
                .order(order)
                .product(product)
                .userId(requestDto.getUserId())
                .rating(requestDto.getRating())
                .comment(requestDto.getComment())
                .build();
        Review saved = reviewRepository.save(review);
        log.info("Review {} created for product {} in order {}", saved.getId(), product.getId(), order.getId());
        return toResponseDto(saved);
    }

    public void deleteReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review with id " + id + " not found"));
        reviewRepository.delete(review);
    }
}
