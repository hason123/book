package com.example.book.dto.ResponseDTO.Post;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostListResponseDTO {
    private Long id;
    private String userPost;
    private String title;
    private int likesCount;
    private int dislikesCount;
    private int commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
