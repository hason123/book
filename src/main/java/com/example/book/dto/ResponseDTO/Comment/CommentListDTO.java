package com.example.book.dto.ResponseDTO.Comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
//POV of ADMIN/USER
public class CommentListDTO {
    private Long commentId;
    private String commentDetail;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private String userComment;
    private String postId;
}
