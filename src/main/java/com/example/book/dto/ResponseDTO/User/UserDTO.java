package com.example.book.dto.ResponseDTO.User;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long userId;
    private String userName;
    private String password;
    private String fullName;
    private String phoneNumber;
    private String identityNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthday;
    private String address;
    private String refreshToken;
    private RoleDTO role;
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoleDTO{
        private Long roleId;
        private String roleName;
    }

}
