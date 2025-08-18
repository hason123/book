package com.example.book.service;

import com.example.book.dto.RequestDTO.RoleRequestDTO;
import com.example.book.dto.ResponseDTO.PageResponseDTO;
import com.example.book.dto.ResponseDTO.RoleResponseDTO;
import org.springframework.data.domain.Pageable;

public interface RoleService {

    RoleResponseDTO updateRole(RoleRequestDTO roleRequestDTO, Long roleId);

    void deleteRole(Long roleId);

    PageResponseDTO<RoleResponseDTO> getPageRole(Pageable pageable);

    RoleResponseDTO getRole(Long roleId);
}
