package com.example.book.controller;

import com.example.book.dto.RequestDTO.BookRequestDTO;
import com.example.book.dto.RequestDTO.Search.SearchBookRequest;
import com.example.book.dto.ResponseDTO.BookResponseDTO;
import com.example.book.dto.ResponseDTO.PageResponseDTO;
import com.example.book.service.impl.BookServiceImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/library")
public class BookController {
    private final BookServiceImpl bookService;

    public BookController(BookServiceImpl bookService) {
        this.bookService = bookService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/books")
    public ResponseEntity<PageResponseDTO<BookResponseDTO>> getAllBooks(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
                                                                        @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize)
    {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);//default first pageNumber is 0
        PageResponseDTO<BookResponseDTO> bookPage = bookService.getBookPage(pageable);
        return ResponseEntity.ok(bookPage);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/book/{id}")
    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable("id") long id) {
        BookResponseDTO bookResponse = bookService.getBookById(id);
        return ResponseEntity.status(HttpStatus.OK).body(bookResponse);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/book/search")
    public ResponseEntity<PageResponseDTO<BookResponseDTO>> searchBook(
            SearchBookRequest searchBookRequest,
            @RequestParam(value = "pageNumber", defaultValue = "1", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "3", required = false) Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        PageResponseDTO<BookResponseDTO> bookSearch = bookService.searchBooks(searchBookRequest, pageable);
        return ResponseEntity.ok(bookSearch);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @PostMapping("/book/create")
    public ResponseEntity<BookResponseDTO> createBook(@RequestBody BookRequestDTO book) {
        BookResponseDTO bookAdded = bookService.addBook(book);
        return ResponseEntity.status(201).body(bookAdded);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @PutMapping("/book/update/{id}")
    public ResponseEntity<BookResponseDTO> updateBook(@PathVariable("id") long id, @RequestBody BookRequestDTO book) {
        BookResponseDTO bookUpdated = bookService.updateBook(id, book);
        return ResponseEntity.status(200).body(bookUpdated);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @DeleteMapping("/book/delete/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable("id") long id) {
        bookService.deleteBookById(id);
        return ResponseEntity.status(200).body("Delete successful");

    }


}
