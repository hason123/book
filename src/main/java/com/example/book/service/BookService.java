package com.example.book.service;

import com.example.book.dto.RequestDTO.BookRequestDTO;
import com.example.book.dto.RequestDTO.Search.SearchBookRequest;
import com.example.book.dto.ResponseDTO.BookResponseDTO;
import com.example.book.dto.ResponseDTO.PageResponseDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface BookService {

    BookResponseDTO addBook(BookRequestDTO request);

    BookResponseDTO getBookById(Long id);

    void deleteBookById(Long id);

    BookResponseDTO updateBook(Long id, BookRequestDTO request);

    PageResponseDTO<BookResponseDTO> getBookPage(Pageable pageable);

    PageResponseDTO<BookResponseDTO> searchBooks(SearchBookRequest searchBookRequest,
                                                 Pageable pageable);

    void exportBookWorkbook(HttpServletResponse response) throws IOException;

    void importExcel(MultipartFile file) throws IOException;
}
