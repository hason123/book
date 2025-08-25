package com.example.book.service.impl;

import com.example.book.config.MessageConfig;
import com.example.book.constant.RoleType;
import com.example.book.dto.RequestDTO.Search.SearchUserRequest;
import com.example.book.dto.RequestDTO.UserRequestDTO;
import com.example.book.dto.RequestDTO.UserRoleRequestDTO;
import com.example.book.dto.ResponseDTO.PageResponseDTO;
import com.example.book.dto.ResponseDTO.User.UserDetailResponseDTO;
import com.example.book.dto.ResponseDTO.User.UserViewResponseDTO;
import com.example.book.entity.*;
import com.example.book.exception.ResourceNotFoundException;
import com.example.book.exception.UnauthorizedException;
import com.example.book.repository.CommentRepository;
import com.example.book.repository.PostRepository;
import com.example.book.repository.RoleRepository;
import com.example.book.repository.UserRepository;
import com.example.book.service.PostService;
import com.example.book.service.UserService;
import com.example.book.specification.UserSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final MessageConfig messageConfig;
    private final PostService postService;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final String USER_NOT_FOUND= "error.user.notfound";
    private final String ACCESS_DENIED= "error.auth.accessDenied";
    private final String USER_NAME_UNIQUE= "error.user.name.unique";
    private final String ROLE_NOT_FOUND= "error.role.notfound";

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, MessageConfig messageConfig, PostService postService, PostRepository postRepository, CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.messageConfig = messageConfig;
        this.postService = postService;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public Object getUserById(Long id){
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new ResourceNotFoundException(messageConfig.getMessage(USER_NOT_FOUND, id));
        }
        if( isCurrentUser(id) || user.getRole().getRoleName().equals(RoleType.ADMIN)) {
            return convertUserDetailToDTO(user);
        }
        else return convertUserViewToDTO(user);
    }

    @Override
    public PageResponseDTO<UserViewResponseDTO> getAllUsers(Pageable pageable){
        Page<User> userPage = userRepository.findAll(pageable);
        Page<UserViewResponseDTO> userResponsePage = userPage.map(this::convertUserViewToDTO);
        PageResponseDTO<UserViewResponseDTO> pageDTO = new PageResponseDTO<>(
                userResponsePage.getNumber() + 1,
                userResponsePage.getNumberOfElements(),
                userResponsePage.getTotalPages(),
                userResponsePage.getContent()
        );
        return pageDTO;
    }

    @Override
    public void deleteUserById(Long id) throws UnauthorizedException {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException(messageConfig.getMessage(USER_NOT_FOUND, id));
        }
        if(isCurrentUser(id) || getCurrentUser().getRole().getRoleName().equals(RoleType.ADMIN)) {
            List<Post> deletedPosts = postRepository.findAllByUser_UserId(id);
            deletedPosts.forEach(post -> {postService.deletePost(post.getPostId());});
            List<Comment> deletedComments = commentRepository.findAllByUser_UserId(id);
            commentRepository.deleteAll(deletedComments);
            userRepository.deleteById(id);
        }
        else throw new UnauthorizedException(messageConfig.getMessage(ACCESS_DENIED));
    }

    @Override
    public UserRequestDTO createUser(UserRequestDTO request){
        User user = new User();
        if(userRepository.existsByUserName(request.getUserName())){
            throw new DataIntegrityViolationException(messageConfig.getMessage(USER_NAME_UNIQUE));
        }
        user.setUserName(request.getUserName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(roleRepository.findByRoleName(RoleType.USER));
        user.setBirthday(request.getBirthday());
        user.setAddress(request.getAddress());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setIdentityNumber(request.getIdentityNumber());
        user.setFullName(request.getFullName());
        userRepository.save(user);
        return convertUserInfoToDTO(user);
    }
    //
    @Override
    public UserRequestDTO updateUser(Long id, UserRequestDTO request) throws UnauthorizedException{
        User updatedUser = userRepository.findById(id).orElse(null);
        if(!isCurrentUser(id) ){
            throw new UnauthorizedException(messageConfig.getMessage(ACCESS_DENIED));
        }
        if(updatedUser == null){
            throw new ResourceNotFoundException(messageConfig.getMessage(USER_NOT_FOUND, id));
        }
        updatedUser.setUserName(request.getUserName());
        updatedUser.setPassword(passwordEncoder.encode(request.getPassword()));
        updatedUser.setRole(roleRepository.findByRoleName(RoleType.USER));
        updatedUser.setBirthday(request.getBirthday());
        updatedUser.setAddress(request.getAddress());
        updatedUser.setPhoneNumber(request.getPhoneNumber());
        updatedUser.setIdentityNumber(request.getIdentityNumber());
        userRepository.save(updatedUser);
        return convertUserInfoToDTO(updatedUser);
    }

    @Override
    public void updateRole(UserRoleRequestDTO userRole){
        if(roleRepository.existsByRoleName(RoleType.valueOf(userRole.getRoleName()))){
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
       else throw new ResourceNotFoundException(messageConfig.getMessage(ROLE_NOT_FOUND));
    }

    @Override
    public PageResponseDTO<UserViewResponseDTO> searchUser(SearchUserRequest request, Pageable pageable){
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
        Page<UserViewResponseDTO> userViewDTOPage = userPage.map(this::convertUserViewToDTO);
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

    @Override
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

    public UserDetailResponseDTO convertUserDetailToDTO(User user){
        UserDetailResponseDTO response = new UserDetailResponseDTO();
        response.setUserRequestDTO(convertUserInfoToDTO(user));
        if (user.getPosts() != null) {
            user.getPosts().forEach(post -> response.getPostIDs().add(post.getPostId()));
        }
        if (user.getComments() != null) {
            user.getComments().forEach(comment -> response.getCommentIDs().add(comment.getCommentId()));
        }
        if (user.getBorrowing() != null) {
            user.getBorrowing().forEach(borrowing -> {
                response.getBorrowingIDs().add(borrowing.getId());});
        }
        return response;
    }

    public UserViewResponseDTO convertUserViewToDTO(User user){
        UserViewResponseDTO userViewDTO = new UserViewResponseDTO();
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