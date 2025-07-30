package com.example.book.dto.ResponseDTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingResponseDTO {
    private Long borrowingId;
    private Date borrowingDate;
    private Date returnDate;

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


}
