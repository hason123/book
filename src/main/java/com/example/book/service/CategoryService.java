package com.example.book.service;

import com.example.book.dto.ResponseDTO.CategoryResponseDTO;
import com.example.book.dto.ResponseDTO.PageResponseDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Pageable;
import java.io.IOException;

public interface CategoryService {

    CategoryResponseDTO getCategory(Long id);

    CategoryResponseDTO addCategory(CategoryResponseDTO category);

    CategoryResponseDTO updateCategory(Long id, CategoryResponseDTO request);

    void deleteCategory(Long id);

    PageResponseDTO<CategoryResponseDTO> getAllCategories(Pageable pageable);

    void createCategoryWorkbook(HttpServletResponse response) throws IOException;
}
