package com.example.book.controller;

import com.example.book.dto.RequestDTO.Search.SearchUserRequest;
import com.example.book.dto.RequestDTO.UserRequestDTO;
import com.example.book.dto.RequestDTO.UserRoleRequestDTO;
import com.example.book.dto.ResponseDTO.PageResponseDTO;
import com.example.book.dto.ResponseDTO.User.UserViewDTO;
import com.example.book.exception.UnauthorizedException;
import com.example.book.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/library")
public class UserController {

    private final UserServiceImpl userService;
    private final UserServiceImpl userServiceImpl;

    public UserController(UserServiceImpl userService, UserServiceImpl userServiceImpl) {
        this.userService = userService;
        this.userServiceImpl = userServiceImpl;
    }
    //GetAll
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/users")
    public ResponseEntity<PageResponseDTO<UserViewDTO>> getAllUsers(
            @RequestParam(value = "pageNumber", defaultValue = "1", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "3", required = false) Integer pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        PageResponseDTO<UserViewDTO> userPage = userService.getAllUsers(pageable);
        return ResponseEntity.ok(userPage); // HTTP 200 + JSON list
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id)  {
        Object user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    //DeleteUser
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/user/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) throws UnauthorizedException {
        userService.deleteUserById(id);
        return ResponseEntity.ok("Delete successful");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/user/create")
    public ResponseEntity<UserRequestDTO> createUser(@Valid @RequestBody UserRequestDTO user) {
        UserRequestDTO userAdded = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userAdded);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/user/update/{id}")
    public ResponseEntity<UserRequestDTO> updateUser( @PathVariable Long id,
                                                   @Valid @RequestBody UserRequestDTO user) throws UnauthorizedException {
        UserRequestDTO updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(updatedUser);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/user/updateRole")
    public ResponseEntity<?> updateRole(@Valid @RequestBody UserRoleRequestDTO userRole){
        userServiceImpl.updateRole(userRole);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/users/search")
    public ResponseEntity<PageResponseDTO<UserViewDTO>> searchUsers(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
                                                                    @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize, SearchUserRequest request){
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        PageResponseDTO<UserViewDTO> results = userServiceImpl.searchUser(request,pageable);
        return ResponseEntity.ok(results);
    }

}
