package com.example.book.service;

import com.example.book.dto.RequestDTO.BorrowingRequestDTO;
import com.example.book.dto.ResponseDTO.BorrowingResponseDTO;
import com.example.book.dto.ResponseDTO.PageResponseDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Pageable;
import java.io.IOException;

public interface BorrowingService {

    BorrowingResponseDTO addBorrowing(BorrowingRequestDTO request);

    BorrowingResponseDTO getBorrowingById(Long id);

    void deleteBookById(Long id);

    BorrowingResponseDTO updateBorrowing(Long id, BorrowingRequestDTO request);

    PageResponseDTO<BorrowingResponseDTO> getBorrowingPage(Pageable pageable);

    void createBorrowingWorkbook(HttpServletResponse response) throws IOException;

}
