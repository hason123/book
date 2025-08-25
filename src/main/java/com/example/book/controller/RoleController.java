package com.example.book.controller;

import com.example.book.dto.RequestDTO.RoleRequestDTO;
import com.example.book.dto.ResponseDTO.PageResponseDTO;
import com.example.book.dto.ResponseDTO.RoleResponseDTO;
import com.example.book.service.impl.RoleServiceImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/library")
public class RoleController {

    private final RoleServiceImpl roleService;

    public RoleController(RoleServiceImpl roleService) {
        this.roleService = roleService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/roles/{id}")
    public ResponseEntity<RoleResponseDTO> updateRole(@PathVariable long id, @RequestBody RoleRequestDTO request) {
        RoleResponseDTO roleUpdated = roleService.updateRole(request, id);
        return ResponseEntity.ok(roleUpdated);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/roles/{id}")
    public ResponseEntity<RoleResponseDTO> getRole(@PathVariable long id) {
        RoleResponseDTO role = roleService.getRole(id);
        return ResponseEntity.ok(role);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/roles/{id}")
    public ResponseEntity<String> deleteRole(@PathVariable long id) {
        roleService.deleteRole(id);
        return ResponseEntity.status(HttpStatus.OK).body("Delete succesful!");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/roles")
    public ResponseEntity<PageResponseDTO<RoleResponseDTO>> getPageRole(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
    @RequestParam(value = "pageSize", required = false, defaultValue = "1") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        PageResponseDTO<RoleResponseDTO> rolePage = roleService.getPageRole(pageable);
        return ResponseEntity.ok(rolePage);
    }


}
