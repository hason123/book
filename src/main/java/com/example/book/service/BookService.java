package com.example.book.service;

import com.example.book.dto.RequestDTO.BookRequestDTO;
import com.example.book.dto.RequestDTO.Search.SearchBookRequest;
import com.example.book.dto.ResponseDTO.BookResponseDTO;
import com.example.book.dto.ResponseDTO.PageResponseDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookService {


    BookResponseDTO addBook(BookRequestDTO request);

    BookResponseDTO getBookById(Long id);

    void deleteBookById(Long id);

    BookResponseDTO updateBook(Long id, BookRequestDTO request);

    List<BookResponseDTO> getAllBooks();

    //List<BookResponseDTO> getAllBooksPage(Pageable pageable);

    PageResponseDTO<BookResponseDTO> getBookPage(Pageable pageable);

    PageResponseDTO<BookResponseDTO> searchBooks(SearchBookRequest searchBookRequest,
                                                 Pageable pageable);



}
