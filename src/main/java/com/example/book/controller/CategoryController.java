package com.example.book.controller;

import com.example.book.dto.ResponseDTO.CategoryResponseDTO;
import com.example.book.dto.ResponseDTO.PageResponseDTO;
import com.example.book.service.impl.CategoryServiceImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/library/")
public class CategoryController {

    private final CategoryServiceImpl categoryServiceImpl;

    public CategoryController( CategoryServiceImpl categoryServiceImpl) {
        this.categoryServiceImpl = categoryServiceImpl;
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/categories")
    public ResponseEntity<PageResponseDTO<CategoryResponseDTO>> getCategories(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
                                                                              @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        PageResponseDTO<CategoryResponseDTO> categoryPage = categoryServiceImpl.getAllCategories(pageable);
        return ResponseEntity.ok(categoryPage);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/category/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategory(@PathVariable long id) {
        CategoryResponseDTO category = categoryServiceImpl.getCategory(id);
        return ResponseEntity.ok(category);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @PostMapping("/category/create")
    public ResponseEntity<CategoryResponseDTO> createCategory(@RequestBody CategoryResponseDTO category) {
        CategoryResponseDTO categoryAdded = categoryServiceImpl.addCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryAdded);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @PutMapping("/category/update/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(@PathVariable long id, @RequestBody CategoryResponseDTO category) {
        CategoryResponseDTO categoryUpdated = categoryServiceImpl.updateCategory(id, category);
        if (categoryUpdated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(categoryUpdated);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @DeleteMapping("/category/delete/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable long id) {
        categoryServiceImpl.getCategory(id);
        return ResponseEntity.status(200).body("Delete success!");
    }
}
