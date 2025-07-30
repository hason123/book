package com.example.book.service.impl;

import com.example.book.dto.ResponseDTO.CategoryResponseDTO;
import com.example.book.entity.Book;
import com.example.book.entity.Category;
import com.example.book.repository.BookRepository;
import com.example.book.repository.CategoryRepository;
import com.example.book.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, BookRepository bookRepository) {
        this.categoryRepository = categoryRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public Optional<Category> getCategory(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public CategoryResponseDTO addCategory(Category category) {
        categoryRepository.save(category);
        return convertEntityToDTO(category);
    }

    @Override
    public CategoryResponseDTO updateCategory(Long id, Category category) {
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        if (categoryOptional.isPresent()) {
            Category categoryUpdated = categoryOptional.get();
            categoryUpdated.setCategoryName(category.getCategoryName());

            // kiểm tra sách đc thêm có hợp lệ không
            List<Book> validBooks = new ArrayList<>();
            for (Book book : category.getBooks()) {
                Optional<Book> foundBook = bookRepository.findById(book.getBookId());
                if (foundBook.isPresent()) {
                    Book bookUpdated = foundBook.get();
                    validBooks.add(bookUpdated);
                }
                else{
                    throw new EntityNotFoundException(
                            "Book with ID " + book.getBookId() + " not found"
                    );
                }
            }

            categoryUpdated.setBooks(validBooks);
            categoryRepository.save(categoryUpdated);
            return convertEntityToDTO(categoryUpdated);
        } else {
            return null;
        }
    }


    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public List<CategoryResponseDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryResponseDTO> categoryResponseDTOS = new ArrayList<>();
        for(Category category : categories){
            CategoryResponseDTO categoryResponseDTO = convertEntityToDTO(category);
            categoryResponseDTOS.add(categoryResponseDTO);
        }
        return categoryResponseDTOS;
    }


    public CategoryResponseDTO convertEntityToDTO(Category category) {
        CategoryResponseDTO categoryDTO = new CategoryResponseDTO();
        categoryDTO.setCategoryName(category.getCategoryName());
        categoryDTO.setCategoryId(category.getCategoryId());
        List<CategoryResponseDTO.BookBasic> bookBasics = new ArrayList<>();
        if(category.getBooks() != null) {
            for (Book book: category.getBooks()) {
                CategoryResponseDTO.BookBasic bookBasic = new CategoryResponseDTO.BookBasic();
                bookBasic.setBookId(book.getBookId());
                bookBasic.setBookName(book.getBookName());
                bookBasics.add(bookBasic);
            }
        }
        categoryDTO.setBookBasic(bookBasics);
        return categoryDTO;
    }
}
