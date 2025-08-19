package com.example.book.service.impl;

import com.example.book.config.MessageConfig;
import com.example.book.constant.RoleType;
import com.example.book.dto.RequestDTO.Search.SearchUserRequest;
import com.example.book.dto.RequestDTO.UserRequestDTO;
import com.example.book.dto.RequestDTO.UserRoleUpdateDTO;
import com.example.book.dto.ResponseDTO.PageResponseDTO;
import com.example.book.dto.ResponseDTO.User.UserDetailDTO;
import com.example.book.dto.ResponseDTO.User.UserViewDTO;
import com.example.book.entity.*;
import com.example.book.exception.NullValueException;
import com.example.book.exception.ResourceNotFoundException;
import com.example.book.exception.UnauthorizedException;
import com.example.book.repository.RoleRepository;
import com.example.book.repository.UserRepository;
import com.example.book.service.UserService;
import com.example.book.specification.UserSpecification;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final MessageConfig messageConfig;
    private final String USER_NOT_FOUND= "error.user.notfound";
    private final String ACCESS_DENIED= "error.auth.access.denied";
    private final String USER_NAME_UNIQUE= "error.user.name.unique";


    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, MessageConfig messageConfig) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.messageConfig = messageConfig;
    }

    //CURRENT USER
    @Override
    public Object getUserById(Long id) throws NullValueException {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new NullValueException(messageConfig.getMessage(USER_NOT_FOUND, id));
        }
        if( isCurrentUser(id) || user.getRole().getRoleName().equals(RoleType.ADMIN)) {
            return convertUserDetailToDTO(user);
        }
        else return convertUserViewToDTO(user);
    }

    //ADMIN OR USER
    @Override
    public PageResponseDTO<UserViewDTO> getAllUsers(Pageable pageable){
        Page<User> userPage = userRepository.findAll(pageable);
        Page<UserViewDTO> userResponsePage = userPage.map(this::convertUserViewToDTO);
        PageResponseDTO<UserViewDTO> pageDTO = new PageResponseDTO<>(
                userResponsePage.getNumber() + 1,
                userResponsePage.getNumberOfElements(),
                userResponsePage.getTotalPages(),
                userResponsePage.getContent()
        );
        return pageDTO;
    }
    //ADMIN
    @Override
    public void deleteUserById(Long id) throws NullValueException {
        if (!userRepository.existsById(id)) {
            throw new NullValueException(messageConfig.getMessage(USER_NOT_FOUND, id));
        }
        userRepository.deleteById(id);
    }

    //ADMIN (thuc ra day la chuc nang register nhung ma admin co the tao nguoi dung khac :))))
    @Override
    public UserRequestDTO createUser(UserRequestDTO userRequest){
        User user = new User();
        if(userRepository.existsByUserName(userRequest.getUserName())){
            throw new DataIntegrityViolationException(messageConfig.getMessage(USER_NAME_UNIQUE));
        }
        user.setUserName(userRequest.getUserName());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setRole(roleRepository.findByRoleName(RoleType.USER));
        user.setBirthday(userRequest.getBirthday());
        user.setAddress(userRequest.getAddress());
        user.setPhoneNumber(userRequest.getPhoneNumber());
        user.setIdentityNumber(userRequest.getIdentityNumber());
        userRepository.save(user);
        return convertUserInfoToDTO(user);
    }
    //
    @Override
    public UserRequestDTO updateUser(Long id, UserRequestDTO userRequest) throws NullValueException, UnauthorizedException{
        User updatedUser = userRepository.findById(id).orElse(null);
        if(!isCurrentUser(id) ){
            throw new UnauthorizedException(messageConfig.getMessage(ACCESS_DENIED));
        }
        if(updatedUser == null){
            throw new NullValueException(messageConfig.getMessage(USER_NOT_FOUND, id));
        }
        updatedUser.setUserName(userRequest.getUserName());
        updatedUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        updatedUser.setRole(roleRepository.findByRoleName(RoleType.USER));
        updatedUser.setBirthday(userRequest.getBirthday());
        updatedUser.setAddress(userRequest.getAddress());
        updatedUser.setPhoneNumber(userRequest.getPhoneNumber());
        updatedUser.setIdentityNumber(userRequest.getIdentityNumber());
        userRepository.save(updatedUser);
        return convertUserInfoToDTO(updatedUser);
    }

    //ADMIN UPDATE ROLE OF ONE OR MORE PEOPLE
    @Override
    public void updateRole(UserRoleUpdateDTO userRole){
        Role roleUpdated = roleRepository.findByRoleName(RoleType.valueOf(userRole.getRoleName()));
        userRole.getUserNames().stream()
                .map(userName -> {
                    User user = userRepository.findByUserName(userName);
                    if (user == null) {
                        throw new ResourceNotFoundException(messageConfig.getMessage(USER_NOT_FOUND, userName));
                    }
                    return user;
                })
                .forEach(user -> {
                    user.setRole(roleUpdated);
                    userRepository.save(user);
                });
    }

    @Override
    public PageResponseDTO<UserViewDTO> searchUser(SearchUserRequest request, Pageable pageable){
        Specification<User> spec = (root, query, cb) -> cb.conjunction();
        String userName = request.getUserName();
        Long userId = request.getUserId();
        String roleName = request.getRoleName();

        if(StringUtils.hasText(userName)){
            spec = spec.and(UserSpecification.likeUserName(userName));
        }
        if(StringUtils.hasText(roleName)){
            spec = spec.and(UserSpecification.hasRole(roleName));
        }
        if(userId != null){
            spec = spec.and(UserSpecification.hasUserID(userId));
        }
        Page<User> userPage =  userRepository.findAll(spec, pageable);
        Page<UserViewDTO> userViewDTOPage = userPage.map(this::convertUserViewToDTO);
        return new PageResponseDTO<>(
                userViewDTOPage.getNumber() + 1,
                userViewDTOPage.getNumberOfElements(),
                userViewDTOPage.getTotalPages(),
                userViewDTOPage.getContent()
        );

    }

    public boolean isCurrentUser(Long userId) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Jwt jwt) {
            String currentUserName = jwt.getClaim("sub"); // decode token de lay phan sub
            Long currentUserId = userRepository.findByUserName(currentUserName).getUserId();
            return currentUserId.equals(userId);
        }
        return false;
    }

    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Jwt jwt) {
            String currentUserName = jwt.getClaim("sub"); // decode token de lay phan sub
            return userRepository.findByUserName(currentUserName);
        }
        return null;
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

    public UserRequestDTO convertUserInfoToDTO(User user){
        UserRequestDTO userDTO = new UserRequestDTO();
        userDTO.setUserName(user.getUserName());
        userDTO.setBirthday(user.getBirthday());
        userDTO.setIdentityNumber(user.getIdentityNumber());
        userDTO.setAddress(user.getAddress());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setFullName(user.getFullName());
        userDTO.setUserId(user.getUserId());
        userDTO.setPassword("HIDDEN");
        userDTO.setRoleName(user.getRole().getRoleName().toString());
        return userDTO;
    }

    public UserDetailDTO convertUserDetailToDTO(User user){
        UserDetailDTO userDetailDTO = new UserDetailDTO();
        userDetailDTO.setUserRequestDTO(convertUserInfoToDTO(user));
        if (user.getPosts() != null) {
            user.getPosts().forEach(post -> userDetailDTO.getPostIDs().add(post.getPostId()));
        }
        if (user.getComments() != null) {
            user.getComments().forEach(comment -> userDetailDTO.getCommentIDs().add(comment.getCommentId()));
        }
        if (user.getBorrowing() != null) {
            user.getBorrowing().forEach(borrowing -> {userDetailDTO.getBorrowingIDs().add(borrowing.getId());});
        }
        return userDetailDTO;
    }

    public UserViewDTO convertUserViewToDTO(User user){
        UserViewDTO userViewDTO = new UserViewDTO();
        userViewDTO.setUserName(user.getUserName());
        userViewDTO.setBirthday(user.getBirthday());
        userViewDTO.setUserId(user.getUserId());
        userViewDTO.setRoleName(user.getRole().getRoleName().toString());
        if (user.getPosts() != null) {
            user.getPosts().forEach(post -> userViewDTO.getPostIDs().add(post.getPostId()));
        }
        if (user.getComments() != null) {
            user.getComments().forEach(comment -> userViewDTO.getCommentIDs().add(comment.getCommentId()));
        }
        return userViewDTO;
    }

}