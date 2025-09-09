package com.example.book.dto.ResponseDTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private String accessToken;
    @JsonIgnore
    private String refreshToken;
    private UserLogin user;
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserLogin {
        private Long id;
        private String username;
        private String role;
    }


}
