package com.example.book.dto.ResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponseDTO {
    private Long categoryId;
    private String categoryName;
    private List<BookBasic> bookBasic = new ArrayList<>();
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BookBasic{
        private Long bookId;
        private String bookName;
        private String author;
    }
}
