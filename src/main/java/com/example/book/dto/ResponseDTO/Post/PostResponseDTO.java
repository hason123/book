package com.example.book.dto.ResponseDTO.Post;

import com.example.book.dto.ResponseDTO.Comment.CommentResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDTO {
    private Long id;
    private String userPost;
    private String title;
    private String content;
    private int likesCount;
    private int dislikesCount;
    private int commentCount;
    private List<CommentResponseDTO> comments;
    private LocalDate createdAt;
    private LocalDate updatedAt;

}
