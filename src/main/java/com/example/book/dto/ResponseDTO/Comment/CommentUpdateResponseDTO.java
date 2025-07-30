package com.example.book.dto.ResponseDTO.Comment;


import com.example.book.dto.ResponseDTO.UserCommentPostDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentUpdateResponseDTO {

    private Long commentId;
    private String commentDetail;
    private Instant updatedAt;

    private UserCommentPostDTO userComment;


}
