package com.example.book.controller;

import com.example.book.dto.RequestDTO.CategoryRequestDTO;
import com.example.book.dto.ResponseDTO.CategoryResponseDTO;
import com.example.book.dto.ResponseDTO.PageResponseDTO;
import com.example.book.service.CategoryService;
import com.example.book.service.impl.CategoryServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/library/")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController( CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "Lấy danh sách thể loại sách")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/categories")
    public ResponseEntity<PageResponseDTO<CategoryResponseDTO>> getCategories(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
                                                                              @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        PageResponseDTO<CategoryResponseDTO> categoryPage = categoryService.getAllCategories(pageable);
        return ResponseEntity.ok(categoryPage);
    }

    @Operation(summary = "Lấy thông tin thể loại sách")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/categories/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategory(@PathVariable long id) {
        CategoryResponseDTO category = categoryService.getCategory(id);
        return ResponseEntity.ok(category);
    }

    @Operation(summary = "Thêm mới thể loại sách")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @PostMapping("/categories")
    public ResponseEntity<CategoryResponseDTO> createCategory(@RequestBody CategoryRequestDTO category) {
        CategoryResponseDTO categoryAdded = categoryService.addCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryAdded);
    }

    @Operation(summary = "Cập nhật thể loại sách")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @PutMapping("/categories/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(@PathVariable long id, @RequestBody CategoryRequestDTO category) {
        CategoryResponseDTO categoryUpdated = categoryService.updateCategory(id, category);
        return ResponseEntity.ok(categoryUpdated);
    }

    @Operation(summary = "Xóa thể loại sách")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable long id) {
        categoryService.deleteCategory(id);
        Map<String, String> response = Map.of("message", "Delete successful");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Thống kê số sách theo thể loại")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/categories/dashboard")
    public ResponseEntity<Void> getCategoryDashboard(HttpServletResponse response) throws IOException {
        categoryService.createCategoryWorkbook(response);
        return ResponseEntity.ok().build();
    }
}
