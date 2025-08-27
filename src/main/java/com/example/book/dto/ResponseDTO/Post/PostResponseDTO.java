package com.example.book.dto.ResponseDTO.Post;

import com.example.book.dto.ResponseDTO.Comment.CommentResponseDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
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
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

}
