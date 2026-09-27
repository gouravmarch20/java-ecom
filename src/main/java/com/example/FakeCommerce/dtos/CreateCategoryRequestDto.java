package com.example.FakeCommerce.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateCategoryRequestDto {

    @NotBlank(message = "Category name is required")
    private String name;
}
