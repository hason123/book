package com.example.book.dto.ResponseDTO.User;

import com.example.book.dto.RequestDTO.UserRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
//ADMIN va nguoi dung ca nhan co the xem duoc
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailResponseDTO {
    private UserRequestDTO userRequestDTO;
    private List<Long> postIDs = new ArrayList<>();
    private List<Long> commentIDs = new ArrayList<>();
    private List<Long> borrowingIDs = new ArrayList<>();
}
