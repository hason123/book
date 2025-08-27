package com.example.book.service.impl;

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
import java.util.Map;
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

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response == null || !response.containsKey("items")) {
            System.out.println("Không có dữ liệu sách từ API");
            return;
        }

        List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");
        List<Book> books = new ArrayList<>();

        for (Map<String, Object> item : items) {
            Map<String, Object> volumeInfo = (Map<String, Object>) item.get("volumeInfo");
            if (volumeInfo == null) continue;
            String title = (String) volumeInfo.get("title");
            List<String> authors = (List<String>) volumeInfo.get("authors");
            String publisher = (String) volumeInfo.get("publisher");
            Integer pageCount = (Integer) volumeInfo.get("pageCount");
            String printType = (String) volumeInfo.get("printType");
            String language = (String) volumeInfo.get("language");
            String description = (String) volumeInfo.get("description");
            List<String> categories = (List<String>) volumeInfo.get("categories");
            Book book = new Book();
            book.setBookName(title);
            book.setAuthor(authors != null ? String.join(", ", authors) : null);
            book.setPublisher(publisher);
            book.setPageCount(pageCount);
            book.setPrintType(printType);
            book.setLanguage(language);
            book.setBookDesc(description);
            book.setQuantity(1);
            List<Category> categoryEntities = new ArrayList<>();
            if (categories != null) {
                for (String catName : categories) {
                    Category category = categoryRepository.findByCategoryName(catName)
                            .orElseGet(() -> {
                                Category newCat = new Category();
                                newCat.setCategoryName(catName);
                                return categoryRepository.save(newCat);
                            });
                    categoryEntities.add(category);
                }
            }
            book.setCategories(categoryEntities);// hoặc mặc định khác
            books.add(book);
        }
        bookRepository.saveAll(books);
        log.info("{} books have been saved to DB", books.size());
    }
}

