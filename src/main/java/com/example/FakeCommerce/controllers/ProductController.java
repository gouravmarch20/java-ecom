package com.example.FakeCommerce.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.FakeCommerce.dtos.ApiResponse;
import com.example.FakeCommerce.dtos.CreateProductRequestDto;
import com.example.FakeCommerce.dtos.GetProductResponseDto;
import com.example.FakeCommerce.dtos.GetProductWithDetailsResponseDto;
import com.example.FakeCommerce.services.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<GetProductResponseDto>>> getAllProducts() {
        logger.info("Fetching all products");
        List<GetProductResponseDto> products = productService.getAllProducts();
        logger.debug("Successfully fetched {} products", products.size());
        return ResponseEntity.ok(ApiResponse.success(products, "Products fetched successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GetProductResponseDto>> getProductById(@PathVariable Long id) {
        logger.info("Fetching product with ID: {}", id);
        GetProductResponseDto product = productService.getProductById(id);
        logger.debug("Product found: {}", product.getTitle());
        return ResponseEntity.ok(ApiResponse.success(product, "Product fetched successfully"));
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<ApiResponse<GetProductWithDetailsResponseDto>> getProductWithDetailsById(@PathVariable Long id) {
        logger.info("Fetching product details with ID: {}", id);
        GetProductWithDetailsResponseDto product = productService.getProductWithDetailsById(id);
        logger.debug("Product details loaded: {} in category {}", product.getTitle(), product.getCategory());
        return ResponseEntity.ok(ApiResponse.success(product, "Product fetched successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GetProductWithDetailsResponseDto>> createProduct(@Valid @RequestBody CreateProductRequestDto requestDto) {
        logger.info("Creating new product: {}", requestDto.getTitle());
        GetProductWithDetailsResponseDto product = productService.createProduct(requestDto);
        logger.info("Product created successfully with ID: {}", product.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(product, "Product created successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        logger.info("Deleting product with ID: {}", id);
        productService.deleteProduct(id);
        logger.info("Product deleted successfully");
        return ResponseEntity.ok(ApiResponse.success(null, "Product deleted successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<GetProductResponseDto>>> getProductsByCategory(@RequestParam("categoryName") String category) {
        logger.info("Searching products by category: {}", category);
        List<GetProductResponseDto> products = productService.getProductsByCategory(category);
        logger.debug("Found {} products in category: {}", products.size(), category);
        return ResponseEntity.ok(ApiResponse.success(products, "Products fetched successfully"));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<String>>> getAllCategories() {
        logger.info("Fetching all categories");
        List<String> categories = productService.getAllCategories();
        logger.debug("Total categories available: {}", categories.size());
        return ResponseEntity.ok(ApiResponse.success(categories, "Categories fetched successfully"));
    }

    @GetMapping("/test")
    public ResponseEntity<ApiResponse<String>> testNewRelicLogs() {
        logger.info("NEW RELIC TEST LOG - This should appear in New Relic Logs UI!");
        logger.warn("This is a WARNING test log for New Relic");
        logger.error("This is an ERROR test log for New Relic");
        logger.debug("This is a DEBUG test log for New Relic");
        return ResponseEntity.ok(ApiResponse.success("Test completed", "New Relic log test executed successfully"));
    }
}
