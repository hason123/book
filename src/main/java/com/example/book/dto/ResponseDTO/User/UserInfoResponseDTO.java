package com.example.book.dto.ResponseDTO.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponseDTO {
    private Long userId;
    private String userName;
    private String password;
    private String fullName;
    private String phoneNumber;
    private String identityNumber;
    private LocalDate birthday;
    private String address;
    private String roleName;
}
