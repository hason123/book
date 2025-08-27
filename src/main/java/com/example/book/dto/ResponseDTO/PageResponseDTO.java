package com.example.book.dto.ResponseDTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDTO<T> {
    private int currentPage;
    private int totalPage;
    private long totalElements;
    private List<T> pageList;

}
