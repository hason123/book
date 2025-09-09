package com.example.book.service.impl;

import com.example.book.config.MessageConfig;
import com.example.book.constant.MessageError;
import com.example.book.constant.RoleType;
import com.example.book.dto.RequestDTO.RoleRequestDTO;
import com.example.book.dto.ResponseDTO.PageResponseDTO;
import com.example.book.dto.ResponseDTO.RoleResponseDTO;
import com.example.book.entity.Permission;
import com.example.book.entity.Role;
import com.example.book.exception.BusinessException;
import com.example.book.exception.ResourceNotFoundException;
import com.example.book.repository.PermissionRepository;
import com.example.book.repository.RoleRepository;
import com.example.book.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
@Slf4j
@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final MessageConfig messageConfig;

    public RoleServiceImpl(RoleRepository roleRepository, PermissionRepository permissionRepository, MessageConfig messageConfig) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.messageConfig = messageConfig;
    }

    @Override
    public RoleResponseDTO updateRole(RoleRequestDTO request, Long roleId) {
        log.info("Update role with id {}", roleId);
        Role role = roleRepository.findById(roleId).orElseThrow(() -> {
            log.error(messageConfig.getMessage(MessageError.ROLE_NOT_FOUND), roleId);
            return new ResourceNotFoundException(messageConfig.getMessage(MessageError.ROLE_NOT_FOUND, roleId));
        });
        role.setRoleDesc(request.getDescription());
        if(request.getPermissionIds() != null) {
            List<Permission> permissions = request.getPermissionIds().stream().map(id -> permissionRepository.findById(id).orElseThrow(()
                    ->{ log.error(messageConfig.getMessage(MessageError.PERMISSION_NOT_FOUND), id);
                return new ResourceNotFoundException(messageConfig.getMessage(MessageError.PERMISSION_NOT_FOUND, id));
            })).toList();
            log.info("Adding or removing permissions of a role");
            role.setPermissions(permissions);
        }
        roleRepository.save(role);
        log.info("Role with id {} has been updated", roleId);
        return convertRoleToDTO(role);
    }

    @Override
    public void deleteRole(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> {
                    log.error(messageConfig.getMessage(MessageError.ROLE_NOT_FOUND), roleId);
                    return new BusinessException(messageConfig.getMessage(MessageError.ROLE_NOT_FOUND, roleId));
                });
        role.getPermissions().forEach(permission -> permission.getRoles().remove(role));
        role.getUsers().forEach(user -> user.setRole(roleRepository.findByRoleName(RoleType.USER)));
        roleRepository.deleteById(roleId);
        log.info("Role with id {} has been deleted", roleId);
    }


    @Override
    public PageResponseDTO<RoleResponseDTO> getPageRole(Pageable pageable) {
       log.info("Get roles with page {}", pageable);
       Page<Role> roles = roleRepository.findAll(pageable);
       Page<RoleResponseDTO> rolePage = roles.map(this::convertRoleToDTO);
       return new PageResponseDTO<>(rolePage.getNumber() + 1,
               rolePage.getNumberOfElements(),
               rolePage.getTotalPages(),
               rolePage.getContent());
    }

    @Override
    public RoleResponseDTO getRole(Long roleId) {
        log.info("Get role with id {}", roleId);
        Role role  = roleRepository.findById(roleId).orElseThrow(() -> {
            log.error(messageConfig.getMessage(MessageError.ROLE_NOT_FOUND), roleId);
            return new ResourceNotFoundException(messageConfig.getMessage(MessageError.ROLE_NOT_FOUND, roleId));
        });
        return convertRoleToDTO(role);
    }

    public RoleResponseDTO convertRoleToDTO(Role role) {
        RoleResponseDTO response = new RoleResponseDTO();
        response.setRoleName(role.getRoleName().toString());
        response.setRoleId(role.getRoleID());
        response.setDescription(role.getRoleDesc());
        List<RoleResponseDTO.PermissionDTO> permissions = role.getPermissions().stream().map(permission ->
                new RoleResponseDTO.PermissionDTO(permission.getId(), permission.getName()))
                .toList();
        response.setPermission(permissions);
        return response;
    }
}
