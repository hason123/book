package com.example.book.dto.ResponseDTO.Post;


import com.example.book.dto.ResponseDTO.Comment.CommentDTO;
import com.example.book.dto.ResponseDTO.UserCommentPostDTO;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
    private Long id;
    private String title;
    private String content;
    private UserCommentPostDTO userPost;
    private List<CommentDTO> comments;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private int likesCount;
    private int commentCount;
    private int dislikesCount;

}
