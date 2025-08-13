package com.example.book.dto.RequestDTO.Search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchUserRequest {
    private String userName;
    private Long userId;
    private String roleName;
   // private LocalDate birthday;
}
