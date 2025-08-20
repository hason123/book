package com.example.book.dto.RequestDTO;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingRequestDTO {
    @NotEmpty(message = "{error.borrowing.date.null}")
    private LocalDate borrowingDate;
    private LocalDate returnDate;
    @NotEmpty(message = "{error.borrowing.userId.invalid}")
    private Long userId;
    @NotEmpty(message = "{error.borrowing.bookId.invalid}")
    private Long bookId;
}
