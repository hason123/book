package com.example.book.service.impl;

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

    public PermissionServiceImpl(PermissionRepository permissionRepository, RoleRepository roleRepository) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public PermissionResponseDTO createPermission(PermissionRequestDTO request) {
        Permission permission = new Permission();
        permission.setName(request.getName());
        permission.setDescription(request.getDescription());
        if (request.getRoleIDs() != null) {
            List<Role> roles = request.getRoleIDs().stream()
                    .map(roleID -> roleRepository.findById(roleID)
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Role not found!")))
                    .toList();
            permission.setRoles(roles);
        }
        permissionRepository.save(permission);
        return convertPermissionToDTO(permission);
    }

    @Override
    public PermissionResponseDTO updatePermission(Long id, PermissionRequestDTO request) {
        Permission permission =  permissionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Permission not found"));
        permission.setName(request.getName());
        permission.setDescription(request.getDescription());
        if (request.getRoleIDs() != null) {
            List<Role> roles = request.getRoleIDs().stream()
                    .map(roleID -> roleRepository.findById(roleID)
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Role not found!")))
                    .toList();
            permission.setRoles(roles);
        }
        permissionRepository.save(permission);
        return convertPermissionToDTO(permission);
    }

    @Override
    public void deletePermission(Long id) {
        if(permissionRepository.findById(id).isPresent()) {
            permissionRepository.deleteById(id);
        }
        else throw new ResourceNotFoundException("Permission not found");
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
        Permission permission =  permissionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Permission not found"));
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
