package com.example.book.service.impl;

import com.example.book.constant.RoleType;
import com.example.book.dto.RequestDTO.UserRoleUpdateDTO;
import com.example.book.dto.ResponseDTO.BorrowingResponseDTO;
import com.example.book.dto.ResponseDTO.Comment.CommentUserDTO;
import com.example.book.dto.ResponseDTO.Post.PostDTO;
import com.example.book.dto.ResponseDTO.Post.PostUserDTO;
import com.example.book.dto.ResponseDTO.User.UserDetailDTO;
import com.example.book.dto.ResponseDTO.User.UserInfoDTO;
import com.example.book.dto.ResponseDTO.User.UserViewDTO;
import com.example.book.entity.*;
import com.example.book.exception.NullValueException;
import com.example.book.repository.RoleRepository;
import com.example.book.repository.UserRepository;
import com.example.book.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

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

    //CURRENTUSER
    @Override
    public Object getUserById(Long id) throws NullValueException {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new NullValueException("User not found");
        }
        if(getCurrentUser().getUserId().equals(user.getUserId())) {
            return convertUserDetailToDTO(user);
        }
        else return convertUserViewToDTO(user);
    }
    //ADMIN OR USER
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    //ADMIN
    @Override
    public void deleteUserById(Long id) throws NullValueException{
        if (!userRepository.existsById(id)) {
            throw new NullValueException("User not found");
        }
        userRepository.deleteById(id);
    }

    @Override
    public UserInfoDTO createUser(User user) {
        Role role = roleRepository.findByRoleName(RoleType.USER);
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return convertUserInfoToDTO(user);
    }

    @Override
    public UserInfoDTO updateUser(Long id, User user) throws NullValueException{
        User updatedUser = userRepository.findById(id).orElse(null); //tra ve 1 proxy
        updatedUser.setUserName(user.getUserName());
        //updatedUser.setPassword(user.getPassword());
        updatedUser.setBirthday(user.getBirthday());
        updatedUser.setIdentityNumber(user.getIdentityNumber());
        updatedUser.setAddress(user.getAddress());
        updatedUser.setBirthday(user.getBirthday());
        updatedUser.setPhoneNumber(user.getPhoneNumber());
        userRepository.save(updatedUser);
        return convertUserInfoToDTO(updatedUser);
    }

    //UPDATE ROLE OF ONE OR MORE PEOPLE
    @Override
    public void updateRole(UserRoleUpdateDTO userRole) {
        Role roleUpdated = roleRepository.findByRoleName(RoleType.valueOf(userRole.getRoleName()));
        userRole.getUserNames().stream()
                .map(userRepository::findByUserName)
                .forEach(user -> {
                    user.setRole(roleUpdated);
                    userRepository.save(user);
                });
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

    public UserInfoDTO convertUserInfoToDTO(User user){
        UserInfoDTO userDTO = new UserInfoDTO();
        userDTO.setUserName(user.getUserName());
        userDTO.setBirthday(user.getBirthday());
        userDTO.setIdentityNumber(user.getIdentityNumber());
        userDTO.setAddress(user.getAddress());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setFullName(user.getFullName());
        userDTO.setUserId(user.getUserId());
        userDTO.setPassword("HIDDEN");
        userDTO.setRoleName(user.getRole().toString());
        return userDTO;
    }

    public UserDetailDTO convertUserDetailToDTO(User user){
        UserDetailDTO userDetailDTO = new UserDetailDTO();
        userDetailDTO.setUserInfoDTO(convertUserInfoToDTO(user));
        List<PostUserDTO> posts = user.getPosts().stream()
                .map(this::convertPost)
                .toList();
        userDetailDTO.setPosts(posts);
        List<CommentUserDTO> comments = user.getComments().stream()
                .map(this::convertComment)
                .toList();
        userDetailDTO.setComments(comments);
        List<UserDetailDTO.BorrowingDTO> borrowings = new ArrayList<>();
        for(Borrowing borrowing : user.getBorrowing()){
            UserDetailDTO.BorrowingDTO borrowingDTO = new UserDetailDTO.BorrowingDTO();
            borrowingDTO.setBorrowingDate(borrowing.getBorrowDate());
            borrowingDTO.setBorrowingId(borrowing.getId());
            borrowingDTO.setReturnDate(borrowing.getReturnDate());
            BorrowingResponseDTO.BookDTO bookDTO = new BorrowingResponseDTO.BookDTO();
            bookDTO.setBookId(borrowing.getBook().getBookId());
            bookDTO.setBookName(borrowing.getBook().getBookName());
            borrowingDTO.setBooks(bookDTO);
            borrowings.add(borrowingDTO);
        }
        userDetailDTO.setBorrowings(borrowings);
        return userDetailDTO;
    }

    public UserViewDTO convertUserViewToDTO(User user){
        UserViewDTO userViewDTO = new UserViewDTO();
        userViewDTO.setUserName(user.getUserName());
        userViewDTO.setBirthday(user.getBirthday());
        userViewDTO.setUserId(user.getUserId());
        userViewDTO.setRoleName(user.getRole().getRoleName().toString());
        List<PostUserDTO> posts = user.getPosts().stream()
                .map(this::convertPost)
                .toList();
        userViewDTO.setPosts(posts);
        List<CommentUserDTO> comments = user.getComments().stream()
                .map(this::convertComment)
                .toList();
        userViewDTO.setComments(comments);
        return userViewDTO;
    }

    private PostUserDTO convertPost(Post post) {
        PostUserDTO dto = new PostUserDTO();
        dto.setId(post.getPostId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setCreatedAt(post.getCreatedDate());
        dto.setUpdatedAt(post.getLastModifiedDate());
        dto.setLikesCount(post.getLikesCount());
        return dto;
    }

    private CommentUserDTO convertComment(Comment comment) {
        CommentUserDTO dto = new CommentUserDTO();
        dto.setCommentId(comment.getCommentId());
        dto.setCreatedAt(comment.getCreatedDate());
        dto.setUpdatedAt(comment.getLastModifiedDate());
        dto.setContent(comment.getCommentDetail());
        return dto;
    }








}