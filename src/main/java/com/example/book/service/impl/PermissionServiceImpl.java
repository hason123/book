package com.example.book.service.impl;

import com.example.book.config.MessageConfig;
import com.example.book.dto.RequestDTO.PermissionRequestDTO;
import com.example.book.dto.ResponseDTO.PageResponseDTO;
import com.example.book.dto.ResponseDTO.PermissionResponseDTO;
import com.example.book.entity.Permission;
import com.example.book.exception.ResourceNotFoundException;
import com.example.book.repository.PermissionRepository;
import com.example.book.repository.RoleRepository;
import com.example.book.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
@Slf4j
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
        log.info("create a new permission");
        Permission permission = new Permission();
        permission.setName(request.getName());
        permission.setDescription(request.getDescription());
        log.info("successfully create a new permission");
        permissionRepository.save(permission);
        return convertPermissionToDTO(permission);
    }

    @Override
    public PermissionResponseDTO updatePermission(Long id, PermissionRequestDTO request) {
        log.info("update permission with id {}", id);
        Permission permission =  permissionRepository.findById(id).orElseThrow(() ->
        {
            log.error(messageConfig.getMessage(PERMISSION_NOT_FOUND), id);
            return new ResourceNotFoundException(messageConfig.getMessage(PERMISSION_NOT_FOUND, id));
        });
        permission.setName(request.getName());
        permission.setDescription(request.getDescription());
        log.info("successfully update permission with id {}", id);
        permissionRepository.save(permission);
        return convertPermissionToDTO(permission);
    }

    @Override
    public void deletePermission(Long id) {
        if(permissionRepository.findById(id).isPresent()) {
            log.info("delete permission with id {}", id);
            permissionRepository.deleteById(id);
        }
        else {
            log.info(messageConfig.getMessage(PERMISSION_NOT_FOUND, id));
            throw new ResourceNotFoundException(messageConfig.getMessage(PERMISSION_NOT_FOUND, id));
        }
    }

    @Override
    public PageResponseDTO<PermissionResponseDTO> getPagePermission(Pageable pageable) {
        log.info("Get permission's page");
        Page<Permission> permissions = permissionRepository.findAll(pageable);
        Page<PermissionResponseDTO> permissionPage = permissions.map(this::convertPermissionToDTO);
        log.info("Return permission's page");
        return new PageResponseDTO<>(permissionPage.getNumber() + 1,
                permissionPage.getNumberOfElements(),
                permissionPage.getTotalPages(),
                permissionPage.getContent());
    }

    @Override
    public PermissionResponseDTO getPermissionById(Long id) {
        log.info("get permission with id {}", id);
        Permission permission =  permissionRepository.findById(id).orElseThrow(() ->
        {
            log.error(messageConfig.getMessage(PERMISSION_NOT_FOUND), id);
            return new ResourceNotFoundException(messageConfig.getMessage(PERMISSION_NOT_FOUND, id));
        });
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
