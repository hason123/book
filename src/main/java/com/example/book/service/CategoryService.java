package com.example.book.service;

import com.example.book.dto.ResponseDTO.CategoryResponseDTO;
import com.example.book.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    Optional<Category> getCategory(Long id);
    CategoryResponseDTO addCategory(Category category);
    CategoryResponseDTO updateCategory(Long id, Category category);
    void deleteCategory(Long id);
    List<CategoryResponseDTO> getAllCategories();
}
