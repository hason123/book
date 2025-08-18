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
public class PermissionRequestDTO {
    private String name;
    private String description;
    private List<Long> roleIDs;
}
