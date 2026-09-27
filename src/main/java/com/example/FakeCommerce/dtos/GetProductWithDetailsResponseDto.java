package com.example.FakeCommerce.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class GetProductWithDetailsResponseDto extends GetProductResponseDto {

    private String category;
}
