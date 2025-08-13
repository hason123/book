package com.example.book.dto.RequestDTO.Search;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class SearchPostRequest {
    private String title;
    private String content;
    private String userName;
    private LocalDate beforeDate;
    private LocalDate afterDate;
}
