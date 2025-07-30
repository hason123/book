package com.example.book.dto.ResponseDTO.Comment;


import com.example.book.dto.ResponseDTO.Post.PostResponseDTO;
import com.example.book.dto.ResponseDTO.Post.PostUpdateResponseDTO;
import com.example.book.dto.ResponseDTO.UserCommentPostDTO;
import lombok.*;

import java.time.Instant;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDTO {
    private Long commentId;
    private String commentDetail;
    private Instant createdAt;
    private Instant updatedAt;


    private UserCommentPostDTO userComment;

    private PostDTO post;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostDTO {
        private Long postId;
        private String postTitle;

        private UserCommentPostDTO userPost;
    }



}
