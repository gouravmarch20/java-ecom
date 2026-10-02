package com.example.FakeCommerce.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.FakeCommerce.dtos.CreateProductRequestDto;
import com.example.FakeCommerce.dtos.GetProductResponseDto;
import com.example.FakeCommerce.dtos.GetProductWithDetailsResponseDto;
import com.example.FakeCommerce.exceptions.ResourceNotFoundException;
import com.example.FakeCommerce.repositories.CategoryRepository;
import com.example.FakeCommerce.repositories.ProductRepository;
import com.example.FakeCommerce.schema.Category;
import com.example.FakeCommerce.schema.Product;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    // ── private helper ─────────────────────────────────────────────────────
    private GetProductResponseDto toResponseDto(Product p) {
        return GetProductResponseDto.builder()
                .id(p.getId())
                .title(p.getTitle())
                .description(p.getDescription())
                .price(p.getPrice())
                .image(p.getImage())
                .rating(p.getRating())
                .build();
    }

    // ── public methods ─────────────────────────────────────────────────────

    public List<GetProductResponseDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::toResponseDto)
                .toList();
    }

    public GetProductResponseDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));
        return toResponseDto(product);
    }

    public GetProductWithDetailsResponseDto getProductWithDetailsById(Long id) {
        Product product = productRepository.findProductWithDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));
        return GetProductWithDetailsResponseDto.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .price(product.getPrice())
                .image(product.getImage())
                .rating(product.getRating())
                .category(product.getCategory().getName())
                .build();
    }

    public GetProductWithDetailsResponseDto createProduct(CreateProductRequestDto requestDto) {
        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category with id " + requestDto.getCategoryId() + " not found"));
        Product product = Product.builder()
                .title(requestDto.getTitle())
                .description(requestDto.getDescription())
                .price(requestDto.getPrice())
                .image(requestDto.getImage())
                .rating(requestDto.getRating())
                .category(category)
                .build();
        Product saved = productRepository.save(product);
        return GetProductWithDetailsResponseDto.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .description(saved.getDescription())
                .price(saved.getPrice())
                .image(saved.getImage())
                .rating(saved.getRating())
                .category(category.getName())
                .build();
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));
        productRepository.delete(product);
    }

    public List<GetProductResponseDto> getProductsByCategory(String categoryName) {
        return productRepository.findByCategory_Name(categoryName).stream()
                .map(this::toResponseDto)
                .toList();
    }

    public List<String> getAllCategories() {
        return productRepository.getAllLinkedCategories();
    }
}
