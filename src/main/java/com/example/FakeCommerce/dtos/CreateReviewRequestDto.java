package com.example.FakeCommerce.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateReviewRequestDto {

    @NotNull(message = "Order id is required")
    private Long orderId;

    @NotNull(message = "Product id is required")
    private Long productId;

    @NotBlank(message = "User id is required")
    @Size(max = 255, message = "User id must be at most 255 characters")
    private String userId;

    @NotNull(message = "Rating is required")
    @DecimalMin(value = "1.0", message = "Rating must be between 1 and 5")
    @DecimalMax(value = "5.0", message = "Rating must be between 1 and 5")
    private BigDecimal rating;

    @NotBlank(message = "Comment is required")
    @Size(max = 255, message = "Comment must be at most 255 characters")
    private String comment;
}
