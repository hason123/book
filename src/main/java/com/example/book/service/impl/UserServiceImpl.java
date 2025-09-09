package com.example.book.service.impl;

import com.example.book.config.MessageConfig;
import com.example.book.constant.MessageError;
import com.example.book.constant.ReactionType;
import com.example.book.constant.RoleType;
import com.example.book.dto.RequestDTO.Search.SearchUserRequest;
import com.example.book.dto.RequestDTO.UserRequestDTO;
import com.example.book.dto.RequestDTO.UserRoleRequestDTO;
import com.example.book.dto.ResponseDTO.PageResponseDTO;
import com.example.book.dto.ResponseDTO.User.UserDetailResponseDTO;
import com.example.book.dto.ResponseDTO.User.UserInfoResponseDTO;
import com.example.book.dto.ResponseDTO.User.UserViewResponseDTO;
import com.example.book.entity.*;
import com.example.book.exception.ResourceNotFoundException;
import com.example.book.exception.UnauthorizedException;
import com.example.book.repository.*;
import com.example.book.service.CommentService;
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
    private final CommentService commentService;
    private final PostReactionRepository postReactionRepository;
    private final CommentReactionRepository commentReactionRepository;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, MessageConfig messageConfig, PostService postService, PostRepository postRepository, CommentRepository commentRepository, CommentService commentService, PostReactionRepository postReactionRepository, CommentReactionRepository commentReactionRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.messageConfig = messageConfig;
        this.postService = postService;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.commentService = commentService;
        this.postReactionRepository = postReactionRepository;
        this.commentReactionRepository = commentReactionRepository;
    }

    @Override
    public Object getUserById(Long id){
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new ResourceNotFoundException(messageConfig.getMessage(MessageError.USER_NOT_FOUND, id));
        }
        if( isCurrentUser(id) || getCurrentUser().getRole().getRoleName().equals(RoleType.ADMIN)) {
            return convertUserDetailToDTO(user);
        }
        else return convertUserViewToDTO(user);
    }

    @Override
    public PageResponseDTO<UserViewResponseDTO> getAllUsers(Pageable pageable){
        Page<User> userPage = userRepository.findAll(pageable);
        Page<UserViewResponseDTO> userResponsePage = userPage.map(this::convertUserViewToDTO);
        return new PageResponseDTO<>(
                userResponsePage.getNumber() + 1,
                userResponsePage.getNumberOfElements(),
                userResponsePage.getTotalPages(),
                userResponsePage.getContent()
        );
    }

    @Override
    public void deleteUserById(Long id) throws UnauthorizedException {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new ResourceNotFoundException(messageConfig.getMessage(MessageError.USER_NOT_FOUND, id));
        }
        if(isCurrentUser(id) || getCurrentUser().getRole().getRoleName().equals(RoleType.ADMIN)) {
            List<Post> deletedPosts = postRepository.findAllByUser_UserId(id);
            deletedPosts.forEach(post -> postService.deletePost(post.getPostId()));
            List<Comment> deletedComments = commentRepository.findAllByUser_UserId(id);
            deletedComments.forEach(comment -> commentService.deleteComment(comment.getCommentId()));
            List<CommentReaction> deletedCommentReacts = commentReactionRepository.findByUser_UserId(id);
            for (CommentReaction cr : deletedCommentReacts) {
                Comment comment = cr.getComment();
                if (cr.getReactionType() == ReactionType.LIKE) {
                    comment.setLikesCount(comment.getLikesCount() - 1);
                } else if (cr.getReactionType() == ReactionType.DISLIKE) {
                    comment.setDislikesCount(comment.getDislikesCount() - 1);
                }
                commentRepository.save(comment);
            }
            commentReactionRepository.deleteAll(deletedCommentReacts);
            List<PostReaction> deletedPostReactions = postReactionRepository.findByUser_UserId(id);
            for (PostReaction pr : deletedPostReactions) {
                Post post = pr.getPost();
                if (pr.getReactionType() == ReactionType.LIKE) {
                    post.setLikesCount(post.getLikesCount() - 1);
                } else if (pr.getReactionType() == ReactionType.DISLIKE) {
                    post.setDislikesCount(post.getDislikesCount() - 1);
                }
                postRepository.save(post);
            }
            postReactionRepository.deleteAll(deletedPostReactions);
            userRepository.deleteById(id);
        }
        else throw new UnauthorizedException(messageConfig.getMessage(MessageError.ACCESS_DENIED));
    }

    @Override
    public UserInfoResponseDTO createUser(UserRequestDTO request){
        User user = new User();
        if(userRepository.existsByUserName(request.getUserName())){
            throw new DataIntegrityViolationException(messageConfig.getMessage(MessageError.USER_NAME_UNIQUE));
        }
        user.setUserName(request.getUserName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        Role role = roleRepository.findByRoleName(RoleType.USER);
        if(role == null){
            throw new ResourceNotFoundException(messageConfig.getMessage(MessageError.ROLE_NOT_FOUND));
        }
        user.setRole(roleRepository.findByRoleName(RoleType.USER));
        user.setBirthday(request.getBirthday());
        user.setAddress(request.getAddress());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setIdentityNumber(request.getIdentityNumber());
        user.setFullName(request.getFullName());
        userRepository.save(user);
        return convertUserInfoToDTO(user);
    }
    //khi cap nhat nguoi dung, phai truyen het tat ca field neu khong tru field nguoi dung cap nhat, cac field khac tra null
    @Override
    public UserInfoResponseDTO updateUser(Long id, UserRequestDTO request) throws UnauthorizedException{
        User updatedUser = userRepository.findById(id).orElse(null);
        if(!isCurrentUser(id) ){
            throw new UnauthorizedException(messageConfig.getMessage(MessageError.ACCESS_DENIED));
        }
        if(updatedUser == null){
            throw new ResourceNotFoundException(messageConfig.getMessage(MessageError.USER_NOT_FOUND, id));
        }
        if (request.getUserName() != null) {
            updatedUser.setUserName(request.getUserName());
        }
        else{
            updatedUser.setUserName(updatedUser.getUserName());
        }
        if (request.getPassword() != null) {
            updatedUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        else updatedUser.setPassword(updatedUser.getPassword());
        if (request.getBirthday() != null) {
            updatedUser.setBirthday(request.getBirthday());
        }
        if (request.getAddress() != null) {
            updatedUser.setAddress(request.getAddress());
        }
        if (request.getPhoneNumber() != null) {
            updatedUser.setPhoneNumber(request.getPhoneNumber());
        }
        else updatedUser.setPhoneNumber(updatedUser.getPhoneNumber());
        if (request.getIdentityNumber() != null) {
            updatedUser.setIdentityNumber(request.getIdentityNumber());
        }
        else updatedUser.setIdentityNumber(updatedUser.getIdentityNumber());
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
                            throw new ResourceNotFoundException(messageConfig.getMessage(MessageError.USER_NOT_FOUND, userName));
                        }
                        return user;
                    })
                    .forEach(user -> {
                        user.setRole(roleUpdated);
                        userRepository.save(user);
                    });
        }
       else throw new ResourceNotFoundException(messageConfig.getMessage(MessageError.ROLE_NOT_FOUND));
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

    @Override
    public User handleGetUserByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    @Override
    public User handleGetUserByUserNameAndRefreshToken(String userName, String refreshToken) {
        return userRepository.findByUserNameAndRefreshToken(userName, refreshToken);
    }

    @Override
    public void updateUserToken(String refreshToken, String userName) {
        User currentUser = handleGetUserByUserName(userName);
        if(currentUser != null) {
            currentUser.setRefreshToken(refreshToken);
            userRepository.save(currentUser);
        }
    }

    public UserInfoResponseDTO convertUserInfoToDTO(User user){
        UserInfoResponseDTO userDTO = new UserInfoResponseDTO();
        userDTO.setUserId(user.getUserId());
        userDTO.setUserName(user.getUserName());
        userDTO.setBirthday(user.getBirthday());
        userDTO.setIdentityNumber(user.getIdentityNumber());
        userDTO.setAddress(user.getAddress());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setFullName(user.getFullName());
        userDTO.setPassword("HIDDEN");
        userDTO.setRoleName(user.getRole().getRoleName().toString());
        return userDTO;
    }

    public UserDetailResponseDTO convertUserDetailToDTO(User user){
        UserDetailResponseDTO response = new UserDetailResponseDTO();
        response.setUserInfo(convertUserInfoToDTO(user));
        if (user.getPosts() != null) {
            user.getPosts().forEach(post -> response.getPostIDs().add(post.getPostId()));
        }
        if (user.getComments() != null) {
            user.getComments().forEach(comment -> response.getCommentIDs().add(comment.getCommentId()));
        }
        if (user.getBorrowing() != null) {
            user.getBorrowing().forEach(borrowing -> response.getBorrowingIDs().add(borrowing.getId()));
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