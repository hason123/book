package com.example.book.dto.ResponseDTO.Comment;

import com.example.book.dto.ResponseDTO.UserCommentPostDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class CommentDTO {
    private Long commentId;
    private String commentDetail;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private UserCommentPostDTO userComment;
}
