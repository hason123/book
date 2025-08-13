package com.example.book.dto.ResponseDTO.User;

import com.example.book.dto.RequestDTO.UserRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
//ADMIN va nguoi dung ca nhan co the xem duoc
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailDTO {
    private UserRequestDTO userRequestDTO;
    private List<Long> postIDs;
    private List<Long> commentIDs;
    private List<Long> borrowingIDs;
}
