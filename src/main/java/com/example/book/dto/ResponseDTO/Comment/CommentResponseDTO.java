package com.example.book.dto.ResponseDTO.Comment;


import lombok.*;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDTO {
    private Long commentId;
    private String commentDetail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String userComment;
    private Integer likes;
    private Integer dislikes;
    private Long parentId;
    private List<CommentResponseDTO> replies;

}

