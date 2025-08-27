package com.example.book.dto.RequestDTO.Search;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchPermissionRequest {
    private String name;
    private String apiPath;
    private String method;
    private String roleName;
    private String description;
}
