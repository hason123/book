package com.example.book.controller;

import com.example.book.dto.RequestDTO.BorrowingRequestDTO;
import com.example.book.dto.ResponseDTO.BorrowingResponseDTO;
import com.example.book.dto.ResponseDTO.PageResponseDTO;
import com.example.book.exception.ResourceNotFoundException;
import com.example.book.service.BorrowingService;
import com.example.book.service.impl.BorrowingServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/library")
public class BorrowingController {
    private final BorrowingService borrowingService;

    public BorrowingController(BorrowingService borrowingService) {
        this.borrowingService = borrowingService;
    }

    @Operation(summary = "Lấy danh sách mượn sách")
    @GetMapping("/borrows")
    public ResponseEntity<?> getBorrowingPage(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                         @RequestParam(value = "pageSize", required = false) Integer pageSize)
    {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize); //da sort trong lop service
        PageResponseDTO<BorrowingResponseDTO> borrowingPage = borrowingService.getBorrowingPage(pageable);
        return ResponseEntity.ok(borrowingPage);
    }

    @Operation(summary = "Lấy thông tin lượt mượn sách")
    @GetMapping("/borrows/{id}")
    public ResponseEntity<BorrowingResponseDTO> getBorrowingById(@PathVariable Long id) throws ResourceNotFoundException {
        BorrowingResponseDTO borrowing = borrowingService.getBorrowingById(id);
        return ResponseEntity.status(HttpStatus.OK).body(borrowing);
    }

    @Operation(summary = "Thêm lượt mượn sách")
    @PostMapping("/borrows")
    public ResponseEntity<BorrowingResponseDTO> addBorrowing(@Valid @RequestBody BorrowingRequestDTO borrowing) {
        BorrowingResponseDTO borrowingAdded = borrowingService.addBorrowing(borrowing);
        return ResponseEntity.status(HttpStatus.CREATED).body(borrowingAdded);
    }

    @Operation(summary = "Cập nhật lượt mượn sách")
    @PutMapping("/borrows/{id}")
    public ResponseEntity<BorrowingResponseDTO> updateBorrowing(@PathVariable Long id, @Valid @RequestBody BorrowingRequestDTO borrowing) {
        BorrowingResponseDTO borrowingUpdated = borrowingService.updateBorrowing(id, borrowing);
        return ResponseEntity.ok(borrowingUpdated);
    }

    @Operation(summary = "Xóa lượt mượn sách")
    @DeleteMapping("/borrows/{id}")
    public ResponseEntity<?> deleteBorrowing(@PathVariable Long id) {
        borrowingService.deleteBorrowingById(id);
        Map<String, String> response = Map.of("message", "Delete successful");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Lấy Top 5 những quyển sách được mượn nhiều nhất")
    @GetMapping("/borrows/dashboard")
    public ResponseEntity<?> getBorrowingDashboard(HttpServletResponse response) throws IOException {
        response.setHeader("Content-Type", "attachment; filename=borrowing.xlsx");
        borrowingService.createBorrowingWorkbook(response);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
