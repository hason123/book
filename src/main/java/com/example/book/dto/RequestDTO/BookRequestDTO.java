package com.example.book.dto.RequestDTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
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
    @NotEmpty(message = "{error.book.name.null}")
    private String bookName;
    @NotEmpty(message = "{error.book.author.null}")
    private String author;
    @NotEmpty(message = "{error.book.publisher.null}")
    private String publisher;
    private int pageCount;
    private String printType;
    private String language;
    @NotEmpty(message = "{error.book.publisher.null}")
    @Pattern(regexp = "^\\d+$", message = "{error.book.quantity.invalid}")
    private int quantity;
    private String bookDesc;
    private List<Long> categoryIDs;
}
