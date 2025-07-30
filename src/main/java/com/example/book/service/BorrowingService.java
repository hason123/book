package com.example.book.service;


import com.example.book.dto.ResponseDTO.BookResponseDTO;
import com.example.book.dto.ResponseDTO.BorrowingResponseDTO;
import com.example.book.dto.ResponseDTO.PageDTO;
import com.example.book.entity.Borrowing;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BorrowingService {
    BorrowingResponseDTO addBorrowing(Borrowing borrowing);
    Optional<Borrowing> getBorrowingById(Long id);
    void deleteBookById(Long id);

    BorrowingResponseDTO updateBook(Long id, Borrowing borrowing);

    PageDTO<BorrowingResponseDTO> getBorrowingPage(Pageable pageable);

    List<BorrowingResponseDTO> getAllBorrowings();

    //boolean borrowBooks(Borrowing borrowing);
}
