package com.example.book.dto.RequestDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.parameters.P;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//ADMIN xem duoc list nguoi dung, can xem chi tiet thi check UserDetail
public class UserRequestDTO {
    @NotEmpty(message = "{error.user.name.null}")
    private String userName;
    @Size(min = 3, message = "{error.user.password.invalid}")
    private String password;
    private String fullName;
    @NotEmpty(message = "{error.user.phoneNumber.null}")
    @Pattern(regexp = "^[0-9]{10}$", message = "{error.user.phoneNumber.invalid}")
    private String phoneNumber;
    @NotEmpty(message = "{error.user.identityNumber.null}")
    @Pattern(regexp = "^[0-9]{12}$", message = "{error.user.identityNumber.invalid}")
    private String identityNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthday;
    private String address;
}
