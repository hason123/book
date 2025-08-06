package com.example.book.dto.ResponseDTO.Post;


import com.example.book.dto.ResponseDTO.UserCommentPostDTO;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostListDTO {
    private Long id;
    private String title;
    private UserCommentPostDTO userPost;
    private int commentCount;
    private int likesCount;
    private int dislikesCount;


}
