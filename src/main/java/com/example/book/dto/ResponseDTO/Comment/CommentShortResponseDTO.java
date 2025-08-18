package com.example.book.dto.ResponseDTO.Comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
//POV of ADMIN/USER
public class CommentShortResponseDTO {
    private Long commentId;
    private String commentDetail;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private String userComment;
    private Integer commentLikes;
    private Integer commentDislikes;
    private String postId;
}
