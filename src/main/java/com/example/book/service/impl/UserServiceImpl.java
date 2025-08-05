package com.example.book.service.impl;

import com.example.book.constant.RoleType;
import com.example.book.dto.ResponseDTO.User.UserDTO;
import com.example.book.entity.Role;
import com.example.book.entity.User;
import com.example.book.repository.RoleRepository;
import com.example.book.repository.UserRepository;
import com.example.book.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }


    @Override
    public UserDTO createUser(User user) {
        Role role = roleRepository.findByRoleName(RoleType.USER)
                .orElseThrow(() -> new RuntimeException("Default role not found"));
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return convertUserToDTO(user);
    }

    public boolean isCurrentUser(Long userId) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof Jwt jwt) {
            String currentUserName = jwt.getClaim("sub"); // hoặc "sub", "id", "uid"... tùy JWT
            Long currentUserId = userRepository.findByUserName(currentUserName).getUserId();
            return currentUserId.equals(userId);
        }
        return false;
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User updateUser(Long id, User user) {
        User updatedUser = userRepository.getReferenceById(id); //tra ve 1 proxy
        updatedUser.setUserName(user.getUserName());
        //updatedUser.setPassword(user.getPassword());
        updatedUser.setBirthday(user.getBirthday());
        updatedUser.setIdentityNumber(user.getIdentityNumber());
        updatedUser.setAddress(user.getAddress());
        updatedUser.setBirthday(user.getBirthday());
        updatedUser.setPhoneNumber(user.getPhoneNumber());
        return userRepository.save(updatedUser);
    }

    public User handleGetUserByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    public User handleGetUserByUserNameAndRefreshToken(String userName, String refreshToken) {
        return userRepository.findByUserNameAndRefreshToken(userName, refreshToken);
    }
    public void updateUserToken(String refreshToken, String userName) {
        User currentUser = handleGetUserByUserName(userName);
        if(currentUser != null) {
            currentUser.setRefreshToken(refreshToken);
            userRepository.save(currentUser);
        }
    }

    public UserDTO convertUserToDTO(User user){
        UserDTO userDTO = new UserDTO();
        userDTO.setUserName(user.getUserName());
        userDTO.setBirthday(user.getBirthday());
        userDTO.setIdentityNumber(user.getIdentityNumber());
        userDTO.setAddress(user.getAddress());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setFullName(user.getFullName());
        userDTO.setUserId(user.getUserId());
        userDTO.setPassword("HIDDEN");
        UserDTO.RoleDTO roleDTO = new UserDTO.RoleDTO();
        roleDTO.setRoleId(user.getRole().getRoleID());
        roleDTO.setRoleName(String.valueOf(user.getRole().getRoleName()));
        userDTO.setRole(roleDTO);
        return userDTO;
    }




}