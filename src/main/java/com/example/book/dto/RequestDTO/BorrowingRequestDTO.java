package com.example.book.dto.RequestDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingRequestDTO {
    @NotEmpty(message = "{error.borrowing.date.null}")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate borrowingDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate returnDate;
    @NotEmpty(message = "{error.borrowing.userId.invalid}")
    private Long userId;
    @NotEmpty(message = "{error.borrowing.bookId.invalid}")
    private Long bookId;
}
