package com.example.book.dto.ResponseDTO;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleBooksResponseDTO {
    private List<GoogleBookItemDTO> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GoogleBookItemDTO {
        private VolumeInfo volumeInfo;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class VolumeInfo {
            private String title;
            private List<String> authors;
            private String publisher;
            private String description;
            private Integer pageCount;
            private String printType;
            private String language;
            private List<String> categories;
        }
    }


}
