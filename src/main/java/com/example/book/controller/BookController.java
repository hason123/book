package com.example.book.controller;

import com.example.book.dto.RequestDTO.BookRequestDTO;
import com.example.book.dto.RequestDTO.Search.SearchBookRequest;
import com.example.book.dto.ResponseDTO.BookResponseDTO;
import com.example.book.dto.ResponseDTO.PageResponseDTO;
import com.example.book.service.impl.BookServiceImpl;
import com.example.book.service.impl.BookSyncService;
import io.swagger.v3.oas.annotations.Operation;
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
    private final BookSyncService bookSyncService;

    public BookController(BookServiceImpl bookService, BookSyncService bookSyncService) {
        this.bookService = bookService;
        this.bookSyncService = bookSyncService;
    }

    @Operation(summary = "Lấy danh sách phân trang sách")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/books")
    public ResponseEntity<PageResponseDTO<BookResponseDTO>> getAllBooks(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
                                                                        @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize)
    {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);//default first pageNumber is 0
        PageResponseDTO<BookResponseDTO> bookPage = bookService.getBookPage(pageable);
        return ResponseEntity.ok(bookPage);
    }

    @Operation(summary = "Lấy thông tin một cuốn sách")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/books/{id}")
    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable("id") long id) {
        BookResponseDTO bookResponse = bookService.getBookById(id);
        return ResponseEntity.status(HttpStatus.OK).body(bookResponse);
    }

    @Operation(summary = "Tìm kiếm sách")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/books/search")
    public ResponseEntity<PageResponseDTO<BookResponseDTO>> searchBook(
            SearchBookRequest searchBookRequest,
            @RequestParam(value = "pageNumber", defaultValue = "1", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "3", required = false) Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        PageResponseDTO<BookResponseDTO> bookSearch = bookService.searchBooks(searchBookRequest, pageable);
        return ResponseEntity.ok(bookSearch);
    }

    @Operation(summary = "Thêm mới cuốn sách")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @PostMapping("/books")
    public ResponseEntity<BookResponseDTO> createBook(@RequestBody BookRequestDTO book) {
        BookResponseDTO bookAdded = bookService.addBook(book);
        return ResponseEntity.status(201).body(bookAdded);
    }

    @Operation(summary = "Cập nhật thông tin cuốn sách")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @PutMapping("/books/{id}")
    public ResponseEntity<BookResponseDTO> updateBook(@PathVariable("id") long id, @RequestBody BookRequestDTO book) {
        BookResponseDTO bookUpdated = bookService.updateBook(id, book);
        return ResponseEntity.status(200).body(bookUpdated);
    }

    @Operation(summary = "Xóa mềm cuốn sách")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @DeleteMapping("/books/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable("id") long id) {
        bookService.deleteBookById(id);
        return ResponseEntity.status(200).body("Delete successful");
    }

    @Operation(summary = "Đồng bộ sách từ API khác ")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @PostMapping("/books/sync")
    public ResponseEntity<Void> syncBook() {
        bookSyncService.syncBooksFromGoogle();
        return ResponseEntity.status(200).build();
    }


}
