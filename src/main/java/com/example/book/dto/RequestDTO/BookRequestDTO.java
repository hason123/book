package com.example.book.dto.RequestDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookRequestDTO {
    private String bookName;
    private String author;
    private String publisher;
    private int pageCount;
    private String printType;
    private String language;
    private int quantity;
    private String bookDesc;
    private List<Long> categoryIDs;
}
