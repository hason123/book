package com.example.book.controller;

import com.example.book.dto.ResponseDTO.CategoryResponseDTO;
import com.example.book.entity.Category;
import com.example.book.service.CategoryService;
import com.example.book.service.impl.CategoryServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/library/")
public class CategoryController {

    private final CategoryServiceImpl categoryServiceImpl;

    public CategoryController( CategoryServiceImpl categoryServiceImpl) {
        this.categoryServiceImpl = categoryServiceImpl;

    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponseDTO>> getCategories() {
        List<CategoryResponseDTO> categories = categoryServiceImpl.getAllCategories();
        if (categories.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategory(@PathVariable long id) {
        Optional<Category> category = categoryServiceImpl.getCategory(id);
        if (category.isPresent()) {
            CategoryResponseDTO categoryResponseDTO = categoryServiceImpl.convertEntityToDTO(category.get());
            return ResponseEntity.ok(categoryResponseDTO);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/category/create")
    public ResponseEntity<CategoryResponseDTO> createCategory(@RequestBody Category category) {

        CategoryResponseDTO categoryAdded = categoryServiceImpl.addCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryAdded);
    }

    @PutMapping("/category/update/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(@PathVariable long id, @RequestBody Category category) {
        CategoryResponseDTO categoryUpdated = categoryServiceImpl.updateCategory(id, category);
        if (categoryUpdated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(categoryUpdated);
    }

    @DeleteMapping("/category/delete/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable long id) {
        Optional<Category> category = categoryServiceImpl.getCategory(id);
        if (category.isPresent()) {
            categoryServiceImpl.deleteCategory(id);
            return ResponseEntity.status(200).body("Delete success!");
        }
        return ResponseEntity.notFound().build();
    }
}
