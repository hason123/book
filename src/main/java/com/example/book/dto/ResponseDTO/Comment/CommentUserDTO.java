package com.example.book.dto.ResponseDTO.Comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentUserDTO {
    private Long commentId;
    private String content;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
