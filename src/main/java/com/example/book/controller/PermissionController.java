package com.example.book.controller;

import com.example.book.dto.RequestDTO.PermissionRequestDTO;
import com.example.book.dto.RequestDTO.Search.SearchPermissionRequest;
import com.example.book.dto.ResponseDTO.PageResponseDTO;
import com.example.book.dto.ResponseDTO.PermissionResponseDTO;
import com.example.book.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/library")
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Operation(summary = "Thêm mới quyền hạn")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/permissions/{id}")
    public ResponseEntity<PermissionResponseDTO> createPermission(@RequestBody PermissionRequestDTO request) {
        PermissionResponseDTO permissionCreated = permissionService.createPermission(request);
        return ResponseEntity.ok(permissionCreated);
    }

    @Operation(summary = "Cập nhật quyền hạn")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/permissions/{id}")
    public ResponseEntity<PermissionResponseDTO> updatePermission(@PathVariable long id, @RequestBody PermissionRequestDTO request) {
        PermissionResponseDTO permissionUpdated = permissionService.updatePermission(id, request);
        return ResponseEntity.ok(permissionUpdated);
    }

    @Operation(summary = "Lấy thông tin quyền hạn")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/permissions/{id}")
    public ResponseEntity<PermissionResponseDTO> getPermission(@PathVariable long id) {
        PermissionResponseDTO permission = permissionService.getPermissionById(id);
        return ResponseEntity.ok(permission);
    }

    @Operation(summary = "Xóa quyền hạn")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/permissions/{id}")
    public ResponseEntity<String> deletePermission(@PathVariable long id) {
        permissionService.deletePermission(id);
        return ResponseEntity.status(HttpStatus.OK).body("Delete succesful!");
    }

    @Operation(summary = "Lấy danh sách quyền hạn có phân trang")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/permissions")
    public ResponseEntity<PageResponseDTO<PermissionResponseDTO>> getPagePermission(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
                                                                        @RequestParam(value = "pageSize", required = false, defaultValue = "1") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        PageResponseDTO<PermissionResponseDTO> permissionPage = permissionService.getPagePermission(pageable);
        return ResponseEntity.ok(permissionPage);
    }

    @Operation(summary = "Tìm kiếm quyền (permissions)")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/search")
    public ResponseEntity<PageResponseDTO<PermissionResponseDTO>> searchPermission(
            @RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
            SearchPermissionRequest request
    ) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        PageResponseDTO<PermissionResponseDTO> permissions = permissionService.searchPermission(pageable, request);
        return ResponseEntity.ok(permissions);
    }

}
