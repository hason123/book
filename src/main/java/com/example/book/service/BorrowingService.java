package com.example.book.service;


import com.example.book.dto.RequestDTO.BorrowingRequestDTO;
import com.example.book.dto.ResponseDTO.BorrowingResponseDTO;
import com.example.book.dto.ResponseDTO.PageResponseDTO;
import org.springframework.data.domain.Pageable;

public interface BorrowingService {

    BorrowingResponseDTO addBorrowing(BorrowingRequestDTO request);

    BorrowingResponseDTO getBorrowingById(Long id);

    void deleteBookById(Long id);

    //don't know if this thing even works
    BorrowingResponseDTO updateBook(Long id, BorrowingRequestDTO request);

    PageResponseDTO<BorrowingResponseDTO> getBorrowingPage(Pageable pageable);



    //boolean borrowBooks(Borrowing borrowing);
}
