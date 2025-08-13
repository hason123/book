package com.example.book.dto.ResponseDTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingResponseDTO {
    private Long borrowingId;
    private LocalDate borrowingDate;
    private LocalDate returnDate;
    /*
    private UserCommentPostDTO userCommentPostDTO;
    private BookDTO bookDTO;
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookDTO{
        private Long bookId;
        private String bookName;
    }
     */
    private String username;
    private String bookName;
    //private String userId;
    //private String bookId;


}
