package com.example.book.dto.ResponseDTO.Post;

import com.example.book.dto.ResponseDTO.UserCommentPostDTO;
import lombok.*;

import java.time.Instant;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostUpdateResponseDTO {
    private Long id;
    private String title;
    private String content;

    private Instant updatedAt;


    private UserCommentPostDTO userPost;

}
