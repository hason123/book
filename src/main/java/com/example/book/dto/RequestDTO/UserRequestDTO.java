package com.example.book.dto.RequestDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//ADMIN xem duoc list nguoi dung, can xem chi tiet thi check UserDetail
public class UserRequestDTO {
    private Long userId;
    @NotEmpty(message = "Username must be filled!")
    private String userName;
    @Size(min = 3)
    private String password;
    private String fullName;
    @Size(min = 12, max = 12, message = "Phone number must be exactly 12 digits")
    private String phoneNumber;
    @Size(min = 12, max = 12, message = "Social Security must be exactly 12 digits")
    private String identityNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthday;
    private String address;
    private String roleName;

}
