package com.example.book.dto.ResponseDTO.User;


import com.example.book.dto.ResponseDTO.Comment.CommentUserDTO;
import com.example.book.dto.ResponseDTO.Post.PostUserDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserViewDTO {
    private Long userId;
    private String userName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthday;
    private List<PostUserDTO> posts;
    private List<CommentUserDTO> comments;
    private String roleName;

}
