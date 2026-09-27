package com.example.FakeCommerce.dtos;

import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
public class GetProductResponseDto {

    private Long id;

    private String title;

    private String description;

    private BigDecimal price;

    private String image;

    private String rating;
}
