package com.example.book.dto.ResponseDTO.User;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
//Nguoi dung phu thong
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserViewDTO {
    private Long userId;
    private String userName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthday;
    private List<Long> postIDs = new ArrayList<>();
    private List<Long> commentIDs = new ArrayList<>();
    private String roleName;

}
