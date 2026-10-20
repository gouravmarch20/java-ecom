package com.example.FakeCommerce.services;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.example.FakeCommerce.cache.CategoryCacheNames;
import com.example.FakeCommerce.dtos.CreateCategoryRequestDto;
import com.example.FakeCommerce.exceptions.ResourceNotFoundException;
import com.example.FakeCommerce.repositories.CategoryRepository;
import com.example.FakeCommerce.repositories.ProductRepository;
import com.example.FakeCommerce.schema.Category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Caching(evict = {
            @CacheEvict(cacheNames = CategoryCacheNames.CATEGORIES, allEntries = true)
    })
    public Category createCategory(CreateCategoryRequestDto requestDto) {
        Category category = Category.builder()
                .name(requestDto.getName())
                .build();
        return categoryRepository.save(category);
    }

    @Cacheable(cacheNames = CategoryCacheNames.CATEGORIES, key = "'all'")
    public List<Category> getAllCategories() {
        log.info("DB HIT --- loading all categories from database");
        return categoryRepository.findAll();
    }

    @Cacheable(cacheNames = CategoryCacheNames.CATEGORY_BY_ID, key = "#id")
    public Category getCategoryById(Long id) {
        log.info("DB HIT --- loading category id={} from database", id);
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category with id " + id + " not found"));
    }

    @Cacheable(cacheNames = CategoryCacheNames.CATEGORY_PRODUCT_COUNT, key = "#id")
    public long getProductCount(Long id) {
        log.info("DB HIT --- counting products for category id={} from database", id);
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category with id " + id + " not found");
        }
        return productRepository.countByCategory_Id(id);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = CategoryCacheNames.CATEGORIES, allEntries = true),
            @CacheEvict(cacheNames = CategoryCacheNames.CATEGORY_BY_ID, key = "#id"),
            @CacheEvict(cacheNames = CategoryCacheNames.CATEGORY_PRODUCT_COUNT, key = "#id")
    })
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category with id " + id + " not found"));
        categoryRepository.delete(category);
    }
}
