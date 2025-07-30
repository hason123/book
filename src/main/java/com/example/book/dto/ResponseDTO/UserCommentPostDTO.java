package com.example.book.dto.ResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class UserCommentPostDTO {
    private Long userId;
    private String userName;

}
