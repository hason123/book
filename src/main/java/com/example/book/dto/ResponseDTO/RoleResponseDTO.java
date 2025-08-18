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
public class RoleResponseDTO {
    private Long roleId;
    private String roleName;
    private String description;
    private List<PermissionDTO> permission;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PermissionDTO{
        private Long permissionId;
        private String permissionName;
    }
}
