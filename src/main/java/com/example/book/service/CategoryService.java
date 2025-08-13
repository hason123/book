package com.example.book.service;

import com.example.book.dto.ResponseDTO.CategoryResponseDTO;
import com.example.book.dto.ResponseDTO.PageResponseDTO;
import org.springframework.data.domain.Pageable;


public interface CategoryService {

    CategoryResponseDTO getCategory(Long id);

    CategoryResponseDTO addCategory(CategoryResponseDTO category);

    CategoryResponseDTO updateCategory(Long id, CategoryResponseDTO request);

    void deleteCategory(Long id);

    PageResponseDTO<CategoryResponseDTO> getAllCategories(Pageable pageable);
}
