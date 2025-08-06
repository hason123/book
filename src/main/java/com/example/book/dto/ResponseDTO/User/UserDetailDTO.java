package com.example.book.dto.ResponseDTO.User;

import com.example.book.dto.ResponseDTO.BorrowingResponseDTO;
import com.example.book.dto.ResponseDTO.Comment.CommentUserDTO;
import com.example.book.dto.ResponseDTO.Post.PostUserDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailDTO {
    private UserInfoDTO userInfoDTO;
    private List<PostUserDTO> posts;
    private List<CommentUserDTO> comments;
    private List<BorrowingDTO> borrowings;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BorrowingDTO{
        private Long borrowingId;
        private LocalDate borrowingDate;
        private LocalDate returnDate;
        private BorrowingResponseDTO.BookDTO books;
    }
}
