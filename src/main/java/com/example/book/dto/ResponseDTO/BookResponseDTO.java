package com.example.book.dto.ResponseDTO;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookResponseDTO {
    private Long bookId;
    private String bookName;
    private String author;
    private String publisher;
    private Integer pageCount;
    private String printType;
    private String language;
    private Integer quantity;
    private String bookDesc;
    private List<CategoryDTO> categories;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryDTO {
        private Long categoryId;
        private String categoryName;
    }
}