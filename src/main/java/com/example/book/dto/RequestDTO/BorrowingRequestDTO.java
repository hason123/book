package com.example.book.dto.RequestDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingRequestDTO {
    private LocalDate borrowingDate;
    private LocalDate returnDate;
    private Long userId;
    private Long bookId;
}
