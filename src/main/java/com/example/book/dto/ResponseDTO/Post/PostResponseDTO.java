package com.example.book.dto.ResponseDTO.Post;


import com.example.book.dto.ResponseDTO.Comment.CommentUpdateResponseDTO;
import com.example.book.dto.ResponseDTO.UserCommentPostDTO;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDTO {
    private Long id;
    private String title;
    private String content;


    private UserCommentPostDTO userPost;
    private List<CommentUpdateResponseDTO> comments;

    private Instant createdAt;
    private Instant updatedAt;


}
