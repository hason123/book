package com.example.book.controller;

import com.example.book.dto.RequestDTO.BorrowingRequestDTO;
import com.example.book.dto.ResponseDTO.BorrowingResponseDTO;
import com.example.book.dto.ResponseDTO.PageResponseDTO;
import com.example.book.exception.ResourceNotFoundException;
import com.example.book.service.impl.BorrowingServiceImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/library")
public class BorrowingController {
    private final BorrowingServiceImpl borrowingServiceImpl;

    public BorrowingController(BorrowingServiceImpl borrowingServiceImpl) {
        this.borrowingServiceImpl = borrowingServiceImpl;

    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @GetMapping("/borrowing")
    public ResponseEntity<?> getBorrowingPage(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                         @RequestParam(value = "pageSize", required = false) Integer pageSize)
    {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize); //da sort trong lop service
        PageResponseDTO<BorrowingResponseDTO> borrowingPage = borrowingServiceImpl.getBorrowingPage(pageable);
        return ResponseEntity.ok(borrowingPage);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @GetMapping("/borrow/{id}")
    public ResponseEntity<BorrowingResponseDTO> getBorrowingById(@PathVariable Long id) throws ResourceNotFoundException {
        BorrowingResponseDTO borrowing = borrowingServiceImpl.getBorrowingById(id);
        return ResponseEntity.status(HttpStatus.OK).body(borrowing);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @PostMapping("/borrow/create")
    public ResponseEntity<BorrowingResponseDTO> addBorrowing(@RequestBody BorrowingRequestDTO borrowing) {
        BorrowingResponseDTO borrowingAdded = borrowingServiceImpl.addBorrowing(borrowing);
        return new ResponseEntity<>(borrowingAdded, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @PutMapping("/borrow/update/{id}")
    public ResponseEntity<BorrowingResponseDTO> updateBorrowing(@PathVariable Long id, @RequestBody BorrowingRequestDTO borrowing) {
        BorrowingResponseDTO borrowingUpdated = borrowingServiceImpl.updateBook(id, borrowing);
        return ResponseEntity.ok(borrowingUpdated);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @DeleteMapping("/borrow/delete/{id}")
    public ResponseEntity<?> deleteBorrowing(@PathVariable Long id) {
        borrowingServiceImpl.deleteBookById(id);
        return ResponseEntity.status(200).body("Delete successful!");
    }
}
