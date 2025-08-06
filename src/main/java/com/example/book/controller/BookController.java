package com.example.book.controller;

import com.example.book.dto.RequestDTO.SearchBookRequest;
import com.example.book.dto.ResponseDTO.BookResponseDTO;
import com.example.book.dto.ResponseDTO.PageDTO;
import com.example.book.entity.Book;
import com.example.book.entity.User;
import com.example.book.service.BookService;
import com.example.book.service.impl.BookServiceImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.relational.core.sql.In;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/library")
public class BookController {
    private final BookServiceImpl bookService;

    public BookController(BookServiceImpl bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/books")
    public ResponseEntity<?> getAllBooks(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                     @RequestParam(value = "pageSize", required = false) Integer pageSize)
    {
        PageDTO<BookResponseDTO> bookPage;
        List<BookResponseDTO> bookList;

        if(pageNumber == null || pageSize == null){
            bookList = bookService.getAllBooks();
        }
        else{
            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);//default first pageNumber is 0
            bookPage = bookService.getBookPage(pageable);
            return ResponseEntity.ok(bookPage);
        }

        return ResponseEntity.ok(bookList);
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/book/{id}")
    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable("id") long id) {
        Optional<Book> book = bookService.getBookById(id);

        if (book.isPresent()) {
            BookResponseDTO bookResponseDTO = bookService.convertBookToDTO(book.get());
            return ResponseEntity.ok(bookResponseDTO);
        }
        return ResponseEntity.status(404).build();

    }

    @GetMapping("/book/search")
    public ResponseEntity<PageDTO<BookResponseDTO>> searchBook(
            SearchBookRequest searchBookRequest,
            @RequestParam(value = "pageNumber", defaultValue = "1", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "3", required = false) Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        PageDTO<BookResponseDTO> bookSearch = bookService.searchBooks(searchBookRequest, pageable);
        return ResponseEntity.ok(bookSearch);
    }

    @PostMapping("/book/create")
    public ResponseEntity<BookResponseDTO> createBook(@RequestBody Book book) {
        BookResponseDTO bookAdded = bookService.addBook(book);

        return ResponseEntity.status(201).body(bookAdded);
    }

    @PutMapping("/book/update/{id}")
    public ResponseEntity<BookResponseDTO> updateBook(@PathVariable("id") long id, @RequestBody Book book) {
        BookResponseDTO bookUpdated = bookService.updateBook(id, book);
        if(bookUpdated == null) {
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.ok(bookUpdated);
    }

    @DeleteMapping("/book/delete/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable("id") long id) {
        Optional<Book> book = bookService.getBookById(id);
        if(book.isPresent()) {
            bookService.deleteBookById(id);
        }
        return ResponseEntity.status(200).body("Delete successful");

    }


}
