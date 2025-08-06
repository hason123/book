package com.example.book.dto.ResponseDTO.Post;

import com.example.book.dto.ResponseDTO.UserCommentPostDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostUserDTO {
    private Long id;
    private String title;
    private String content;
    private UserCommentPostDTO userPost;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private int likesCount;
    private int commentCount;
    private int dislikesCount;
}
