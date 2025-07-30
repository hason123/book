package com.example.book.dto.ResponseDTO.Post;


import com.example.book.dto.ResponseDTO.UserCommentPostDTO;
import com.example.book.entity.Post;
import lombok.*;

import java.time.Instant;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateResponseDTO {
    private Long id;
    private String title;
    private String content;

    private Instant createdAt;

    private UserCommentPostDTO userPost;

}
