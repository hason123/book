package com.example.book.dto.ResponseDTO.Comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
//POV of ADMIN/USER
public class CommentShortResponseDTO {
    private Long commentId;
    private String commentDetail;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private String userComment;
    private Integer commentLikes;
    private Long postId;
    private Integer commentDislikes;

}
