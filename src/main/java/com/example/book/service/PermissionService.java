package com.example.book.service;

import com.example.book.dto.RequestDTO.PermissionRequestDTO;
import com.example.book.dto.RequestDTO.Search.SearchPermissionRequest;
import com.example.book.dto.ResponseDTO.PageResponseDTO;
import com.example.book.dto.ResponseDTO.PermissionResponseDTO;
import org.springframework.data.domain.Pageable;

public interface PermissionService {
    PermissionResponseDTO createPermission(PermissionRequestDTO request);

    PermissionResponseDTO updatePermission(Long id, PermissionRequestDTO request);

    void deletePermission(Long id);

    PageResponseDTO<PermissionResponseDTO> getPagePermission(Pageable pageable);

    PermissionResponseDTO getPermissionById(Long id);

    PageResponseDTO<PermissionResponseDTO> searchPermission(Pageable pageable, SearchPermissionRequest request);
}


