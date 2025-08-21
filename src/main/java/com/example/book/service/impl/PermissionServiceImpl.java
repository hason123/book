package com.example.book.service.impl;

import com.example.book.config.MessageConfig;
import com.example.book.dto.RequestDTO.PermissionRequestDTO;
import com.example.book.dto.ResponseDTO.PageResponseDTO;
import com.example.book.dto.ResponseDTO.PermissionResponseDTO;
import com.example.book.entity.Permission;
import com.example.book.entity.Role;
import com.example.book.exception.ResourceNotFoundException;
import com.example.book.repository.PermissionRepository;
import com.example.book.repository.RoleRepository;
import com.example.book.service.PermissionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final MessageConfig messageConfig;
    private final static String ROLE_NOT_FOUND = "error.role.notfound";
    private final static String PERMISSION_NOT_FOUND = "error.permission.notfound";

    public PermissionServiceImpl(PermissionRepository permissionRepository, RoleRepository roleRepository, MessageConfig messageConfig) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.messageConfig = messageConfig;
    }

    @Override
    public PermissionResponseDTO createPermission(PermissionRequestDTO request) {
        Permission permission = new Permission();
        permission.setName(request.getName());
        permission.setDescription(request.getDescription());
        permissionRepository.save(permission);
        return convertPermissionToDTO(permission);
    }

    @Override
    public PermissionResponseDTO updatePermission(Long id, PermissionRequestDTO request) {
        Permission permission =  permissionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(messageConfig.getMessage(PERMISSION_NOT_FOUND, id)));
        permission.setName(request.getName());
        permission.setDescription(request.getDescription());
        permissionRepository.save(permission);
        return convertPermissionToDTO(permission);
    }

    @Override
    public void deletePermission(Long id) {
        if(permissionRepository.findById(id).isPresent()) {
            permissionRepository.deleteById(id);
        }
        else throw new ResourceNotFoundException(messageConfig.getMessage(PERMISSION_NOT_FOUND, id));
    }

    @Override
    public PageResponseDTO<PermissionResponseDTO> getPagePermission(Pageable pageable) {
        Page<Permission> permissions = permissionRepository.findAll(pageable);
        Page<PermissionResponseDTO> permissionPage = permissions.map(this::convertPermissionToDTO);
        return new PageResponseDTO<>(permissionPage.getNumber() + 1,
                permissionPage.getNumberOfElements(),
                permissionPage.getTotalPages(),
                permissionPage.getContent());
    }

    @Override
    public PermissionResponseDTO getPermissionById(Long id) {
        Permission permission =  permissionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(messageConfig.getMessage(PERMISSION_NOT_FOUND, id)));
        return convertPermissionToDTO(permission);
    }

    public PermissionResponseDTO convertPermissionToDTO(Permission permission) {
        PermissionResponseDTO permissionResponseDTO = new PermissionResponseDTO();
        permissionResponseDTO.setId(permission.getId());
        permissionResponseDTO.setName(permission.getName());
        permissionResponseDTO.setDescription(permission.getDescription());
        return permissionResponseDTO;
    }
}
