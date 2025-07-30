package com.example.book.controller;


import com.example.book.dto.ResponseDTO.ApiResponse;
import com.example.book.dto.ResponseDTO.BookResponseDTO;
import com.example.book.dto.ResponseDTO.BorrowingResponseDTO;
import com.example.book.dto.ResponseDTO.PageDTO;
import com.example.book.entity.Borrowing;
import com.example.book.exception.ResourceNotFoundException;
import com.example.book.service.BorrowingService;
import com.example.book.service.impl.BorrowingServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/library")
public class BorrowingController {
    private final BorrowingServiceImpl borrowingServiceImpl;


    public BorrowingController(BorrowingServiceImpl borrowingServiceImpl) {
        this.borrowingServiceImpl = borrowingServiceImpl;

    }

  //  @GetMapping("/borrowings")
  //  public ResponseEntity<List<BorrowingResponseDTO>> getAllBorrowings() {
  //      List<BorrowingResponseDTO> borrowList = borrowingServiceImpl.getAllBorrowings();
  //      return new ResponseEntity<>(borrowList, HttpStatus.OK);
 //   }

    @GetMapping("/borrowing")
    public ResponseEntity<?> getBorrowingPage(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                         @RequestParam(value = "pageSize", required = false) Integer pageSize)
    {
        PageDTO<BorrowingResponseDTO> borrowingPage;
        List<BorrowingResponseDTO> borrowingList;

        if(pageNumber == null || pageSize == null){
            borrowingList = borrowingServiceImpl.getAllBorrowings();
        }
        else{
            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by("returnDate").ascending()); //lay theo cot trong java
           // Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);//default first pageNumber is 0
            borrowingPage = borrowingServiceImpl.getBorrowingPage(pageable);
            return ResponseEntity.ok(borrowingPage);
        }

        return ResponseEntity.ok(borrowingList);
    }

    @GetMapping("/borrow/{id}")
    public ResponseEntity<BorrowingResponseDTO> getBorrowingById(@PathVariable Long id) throws ResourceNotFoundException {
        Optional<Borrowing> borrowing = borrowingServiceImpl.getBorrowingById(id);
        if (borrowing.isEmpty()) {
           throw new ResourceNotFoundException("Không tồn tại bản ghi!");
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/borrow/create")
    public ResponseEntity<BorrowingResponseDTO> addBorrowing(@RequestBody Borrowing borrowing) {
        BorrowingResponseDTO borrowingAdded = borrowingServiceImpl.addBorrowing(borrowing);
        return new ResponseEntity<>(borrowingAdded, HttpStatus.CREATED);
    }

    @PutMapping("/borrow/update/{id}")
    public ResponseEntity<BorrowingResponseDTO> updateBorrowing(@PathVariable Long id, @RequestBody Borrowing borrowing) {
        BorrowingResponseDTO borrowingUpdated = borrowingServiceImpl.updateBook(id, borrowing);
        return ResponseEntity.ok(borrowingUpdated);
    }

    @DeleteMapping("/borrow/delete/{id}")
    public ResponseEntity<?> deleteBorrowing(@PathVariable Long id) {
        Optional<Borrowing> borrowingDeleted = borrowingServiceImpl.getBorrowingById(id);
        if(borrowingDeleted.isPresent()) {
            borrowingServiceImpl.deleteBookById(id);
            return ResponseEntity.status(200).body("Delete successful!");
        }
        return ResponseEntity.status(404).body("There's no data to delete!");
    }
}
