package com.example.book.dto.RequestDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//ADMIN xem duoc list nguoi dung, can xem chi tiet thi check UserDetail
public class UserRequestDTO {
    private Long userId;
    private String userName;
    private String password;
    private String fullName;
    private String phoneNumber;
    private String identityNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthday;
    private String address;
    //private String refreshToken;
    private String roleName;

}
