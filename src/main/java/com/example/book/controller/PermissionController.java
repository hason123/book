package com.example.book.controller;

import com.example.book.dto.RequestDTO.PermissionRequestDTO;
import com.example.book.dto.ResponseDTO.PageResponseDTO;
import com.example.book.dto.ResponseDTO.PermissionResponseDTO;
import com.example.book.service.PermissionService;
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

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/permissions/{id}")
    public ResponseEntity<PermissionResponseDTO> createPermission(@RequestBody PermissionRequestDTO request) {
        PermissionResponseDTO permissionCreated = permissionService.createPermission(request);
        return ResponseEntity.ok(permissionCreated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/permissions/{id}")
    public ResponseEntity<PermissionResponseDTO> updatePermission(@PathVariable long id, @RequestBody PermissionRequestDTO request) {
        PermissionResponseDTO permissionUpdated = permissionService.updatePermission(id, request);
        return ResponseEntity.ok(permissionUpdated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/permissions/{id}")
    public ResponseEntity<PermissionResponseDTO> getPermission(@PathVariable long id) {
        PermissionResponseDTO permission = permissionService.getPermissionById(id);
        return ResponseEntity.ok(permission);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/permissions/{id}")
    public ResponseEntity<String> deletePermission(@PathVariable long id) {
        permissionService.deletePermission(id);
        return ResponseEntity.status(HttpStatus.OK).body("Delete succesful!");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/permissions")
    public ResponseEntity<PageResponseDTO<PermissionResponseDTO>> getPagePermission(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
                                                                        @RequestParam(value = "pageSize", required = false, defaultValue = "1") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        PageResponseDTO<PermissionResponseDTO> permissionPage = permissionService.getPagePermission(pageable);
        return ResponseEntity.ok(permissionPage);
    }
}
