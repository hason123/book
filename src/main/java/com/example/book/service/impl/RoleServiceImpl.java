package com.example.book.service.impl;

import com.example.book.config.MessageConfig;
import com.example.book.dto.RequestDTO.RoleRequestDTO;
import com.example.book.dto.ResponseDTO.PageResponseDTO;
import com.example.book.dto.ResponseDTO.RoleResponseDTO;
import com.example.book.entity.Permission;
import com.example.book.entity.Role;
import com.example.book.exception.ResourceNotFoundException;
import com.example.book.repository.PermissionRepository;
import com.example.book.repository.RoleRepository;
import com.example.book.service.RoleService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final MessageConfig messageConfig;
    private final static String ROLE_NOT_FOUND_CODE = "error.role.notfound";

    public RoleServiceImpl(RoleRepository roleRepository, PermissionRepository permissionRepository, MessageConfig messageConfig) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.messageConfig = messageConfig;
    }

    @Override
    public RoleResponseDTO updateRole(RoleRequestDTO request, Long roleId) {
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new ResourceNotFoundException(messageConfig.getMessage(ROLE_NOT_FOUND_CODE, roleId)));
        role.setRoleDesc(request.getDescription());
        if(request.getPermissionIds() != null) {
            List<Permission> permissions = request.getPermissionIds().stream().map(id -> permissionRepository.findById(id).orElseThrow(()
                    -> new ResourceNotFoundException("Permission not found"))).toList();
            role.setPermissions(permissions);
        }
        roleRepository.save(role);
        return convertRoleToDTO(role);
    }

    @Override
    public void deleteRole(Long roleId) {
        if(roleRepository.findById(roleId).isPresent()) {
            roleRepository.deleteById(roleId);
        }
        else throw new ResourceNotFoundException("Role not found");
    }

    @Override
    public PageResponseDTO<RoleResponseDTO> getPageRole(Pageable pageable) {
       Page<Role> roles = roleRepository.findAll(pageable);
       Page<RoleResponseDTO> rolePage = roles.map(this::convertRoleToDTO);
       return new PageResponseDTO<>(rolePage.getNumber() + 1,
               rolePage.getNumberOfElements(),
               rolePage.getTotalPages(),
               rolePage.getContent());
    }

    @Override
    public RoleResponseDTO getRole(Long roleId) {
        Role role  = roleRepository.findById(roleId).orElseThrow(() -> new ResourceNotFoundException("Role not found"));
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
