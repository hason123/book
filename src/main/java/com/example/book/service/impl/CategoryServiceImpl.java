package com.example.book.service.impl;

import com.example.book.dto.ResponseDTO.CategoryResponseDTO;
import com.example.book.dto.ResponseDTO.PageResponseDTO;
import com.example.book.entity.Category;
import com.example.book.exception.ResourceNotFoundException;
import com.example.book.repository.BookRepository;
import com.example.book.repository.CategoryRepository;
import com.example.book.service.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, BookRepository bookRepository) {
        this.categoryRepository = categoryRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public CategoryResponseDTO getCategory(Long id) {
        Category category = categoryRepository.findById(id).orElse(null);
        if(category == null){
            throw new ResourceNotFoundException("Category with" + id + "not found");
        }
        return convertEntityToDTO(category);
    }

    @Override
    public CategoryResponseDTO addCategory(CategoryResponseDTO request) {
        Category category = new Category();
        category.setCategoryName(request.getCategoryName());
        categoryRepository.save(category);
        return convertEntityToDTO(category);
    }

    @Override
    public CategoryResponseDTO updateCategory(Long id, CategoryResponseDTO request) {
        Category updatedCategory = categoryRepository.findById(id).orElse(null);
        if(updatedCategory == null){
            throw new ResourceNotFoundException("Category with" + id + "not found");
        }
        updatedCategory.setCategoryName(request.getCategoryName());
        categoryRepository.save(updatedCategory);
        return convertEntityToDTO(updatedCategory);
    }


    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category with" + id + "not found"));
        categoryRepository.deleteById(id);
    }

    @Override
    public PageResponseDTO<CategoryResponseDTO> getAllCategories(Pageable pageable) {
        Page<Category> categories = categoryRepository.findAll(pageable);
        Page<CategoryResponseDTO> categoryDTO = categories.map(category -> convertEntityToDTO(category));
        PageResponseDTO<CategoryResponseDTO> categoryPage = new PageResponseDTO<>(
                categoryDTO.getNumber() + 1,
                categoryDTO.getNumberOfElements(),
                categoryDTO.getTotalPages(),
                categoryDTO.getContent()
        );
        return categoryPage;
    }


    public CategoryResponseDTO convertEntityToDTO(Category category) {
        CategoryResponseDTO categoryDTO = new CategoryResponseDTO();
        categoryDTO.setCategoryName(category.getCategoryName());
        categoryDTO.setCategoryId(category.getCategoryId());
        List<CategoryResponseDTO.BookBasic> bookBasics =
            Optional.ofNullable(category.getBooks())
                    .orElseGet(Collections::emptyList)
                    .stream()
                    .map(book -> new CategoryResponseDTO.BookBasic(book.getBookId(), book.getBookName(), book.getAuthor()))
                    .collect(Collectors.toList());
        categoryDTO.setBookBasic(bookBasics);
        return categoryDTO;
    }




}
