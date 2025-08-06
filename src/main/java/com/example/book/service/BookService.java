package com.example.book.service;

import com.example.book.dto.RequestDTO.SearchBookRequest;
import com.example.book.dto.ResponseDTO.BookResponseDTO;
import com.example.book.dto.ResponseDTO.PageDTO;
import com.example.book.entity.Book;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BookService {


    BookResponseDTO addBook(Book book);

    Optional<Book> getBookById(Long id);

    void deleteBookById(Long id);

    BookResponseDTO updateBook(Long id, Book book);

    List<BookResponseDTO> getAllBooks();

    //List<BookResponseDTO> getAllBooksPage(Pageable pageable);

    PageDTO<BookResponseDTO> getBookPage(Pageable pageable);

    PageDTO<BookResponseDTO> searchBooks(SearchBookRequest searchBookRequest,
                                         Pageable pageable);

}
