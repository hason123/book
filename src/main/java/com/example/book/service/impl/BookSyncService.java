package com.example.book.service.impl;

import com.example.book.dto.ResponseDTO.GoogleBooksResponseDTO;
import com.example.book.entity.Book;
import com.example.book.entity.Category;
import com.example.book.repository.BookRepository;
import com.example.book.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class BookSyncService {
    private final RestTemplate restTemplate;
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    public void syncBooksFromGoogle() {
        log.info("Starting sync books from google");
        String url = "https://www.googleapis.com/books/v1/volumes?q=java";
        GoogleBooksResponseDTO response = restTemplate.getForObject(url, GoogleBooksResponseDTO.class);
        if (response == null) {
            System.out.println("Không có dữ liệu sách từ API");
            return;
        }
        List<Book> books = new ArrayList<>();
        for (GoogleBooksResponseDTO.GoogleBookItemDTO item : response.getItems()) {
            GoogleBooksResponseDTO.GoogleBookItemDTO.VolumeInfo info = item.getVolumeInfo();
            if (info == null) continue;
            Book book = new Book();
            if(bookRepository.existsByBookName(info.getTitle())){
                continue;
            }
            book.setBookName(info.getTitle());
            book.setAuthor(info.getAuthors() != null && !info.getAuthors().isEmpty() ? String.join(", ", info.getAuthors()) : null);
            book.setPublisher(info.getPublisher());
            book.setPageCount(info.getPageCount());
            book.setPrintType(info.getPrintType());
            book.setLanguage(info.getLanguage());
            book.setBookDesc(info.getDescription());
            List<String> categories = info.getCategories();
            List<Category> categoryEntities = new ArrayList<>();
            if (categories != null) {
                for (String categoryName : categories) {
                    Category category = categoryRepository.findByCategoryName(categoryName)
                            .orElseGet(() -> {
                                Category newCategory = new Category();
                                newCategory.setCategoryName(categoryName);
                                return categoryRepository.save(newCategory);
                            });
                    categoryEntities.add(category);
                }
            }
            book.setCategories(categoryEntities);
            book.setQuantity(20);
            books.add(book);
        }
        log.info("{} books have been saved to DB", books.size());
    }
}

