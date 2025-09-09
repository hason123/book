package com.example.book.dto.ResponseDTO.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailResponseDTO {
    private UserInfoResponseDTO userInfo;
    private List<Long> postIDs = new ArrayList<>();
    private List<Long> commentIDs = new ArrayList<>();
    private List<Long> borrowingIDs = new ArrayList<>();
}
