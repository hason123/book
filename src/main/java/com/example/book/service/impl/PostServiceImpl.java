package com.example.book.service.impl;

import com.example.book.config.MessageConfig;
import com.example.book.constant.RoleType;
import com.example.book.dto.RequestDTO.PostRequestDTO;
import com.example.book.dto.RequestDTO.Search.SearchPostRequest;
import com.example.book.dto.ResponseDTO.PageResponseDTO;
import com.example.book.dto.ResponseDTO.Post.PostListResponseDTO;
import com.example.book.dto.ResponseDTO.Post.PostResponseDTO;
import com.example.book.entity.Comment;
import com.example.book.entity.Post;
import com.example.book.entity.User;
import com.example.book.exception.ResourceNotFoundException;
import com.example.book.exception.UnauthorizedException;
import com.example.book.repository.CommentRepository;
import com.example.book.repository.PostRepository;
import com.example.book.repository.UserRepository;
import com.example.book.service.CommentService;
import com.example.book.service.PostService;
import com.example.book.service.UserService;
import com.example.book.specification.PostSpecification;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Slf4j
@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final CommentService commentService;
    private final MessageConfig messageConfig;
    private final String POST_NOT_FOUND = "error.post.notfound";
    private final String ACCESS_DENIED = "error.auth.accessDenied";


    public PostServiceImpl(PostRepository postRepository, CommentRepository commentRepository, UserRepository userRepository, @Lazy UserService userService, @Lazy CommentService commentService, MessageConfig messageConfig) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.commentService = commentService;
        this.messageConfig = messageConfig;
    }

    @Override
    public PostResponseDTO addPost(PostRequestDTO request) {
        log.info("Adding post to database");
        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setUser(userService.getCurrentUser());
        postRepository.save(post);
        log.info("Saved post to database");
        return convertPostToDTO(post);
    }

    @Override
    public PostResponseDTO getPost(Long id) {
        log.info("Getting post by id: {}", id);
        Post post = postRepository.findById(id).orElseThrow(() ->
        {
            log.error(messageConfig.getMessage(POST_NOT_FOUND));
            return new ResourceNotFoundException(messageConfig.getMessage(POST_NOT_FOUND, id));
        });
        log.info("Retrieved post by id: {}", id);
        return convertPostToDTO(post);
    }

    @Override
    public PageResponseDTO<PostListResponseDTO> getAllPosts(Pageable pageable) {
       log.info("Getting all posts from database");
       Page<Post> posts = postRepository.findAll(pageable);
       Page<PostListResponseDTO> postList = posts.map(this::convertPostListToDTO);
       log.info("Retrieved all posts from database");
       return new PageResponseDTO<>(postList.getNumber() + 1, postList.getTotalPages(),
                postList.getNumberOfElements(), postList.getContent());
    }

    @Override
    public PageResponseDTO<PostListResponseDTO> searchPost(Pageable pageable, SearchPostRequest request){
        log.info("Searching posts from database");
        Specification<Post> spec = ((root, query, criteriaBuilder) -> criteriaBuilder.conjunction());
        String title = request.getTitle();
        String content = request.getContent();
        String userName = request.getUserName();
        LocalDate beforeDate = request.getBeforeDate();
        LocalDate afterDate = request.getAfterDate();
        if(StringUtils.hasText(title)){
            spec = spec.and(PostSpecification.likeTitle(title));
        }
        if(StringUtils.hasText(content)){
            spec = spec.and(PostSpecification.likeContent(content));
        }
        if(StringUtils.hasText(userName)){
            spec = spec.and(PostSpecification.hasUser(userName));
        }
        if(beforeDate != null){
            spec = spec.and(PostSpecification.uploadBeforeDate(beforeDate));
        }
        if(afterDate != null){
            spec = spec.and(PostSpecification.uploadAfterDate(afterDate));
        }
        Page<Post> posts = postRepository.findAll(spec, pageable);
        Page<PostListResponseDTO> postList = posts.map(this::convertPostListToDTO);
        log.info("Retrieved all posts from database");
        return new PageResponseDTO<>(postList.getNumber() + 1, postList.getTotalPages(),
                postList.getNumberOfElements(), postList.getContent());
    }

    @Override
    public PostResponseDTO updatePost(Long id, PostRequestDTO post) throws UnauthorizedException {
        log.info("Updating post with {} from database", id);
        Post updatedPost = postRepository.findById(id).orElseThrow(() ->
        {
            log.error(messageConfig.getMessage(POST_NOT_FOUND));
            return new ResourceNotFoundException(messageConfig.getMessage(POST_NOT_FOUND, id));
        });
        if(userService.getCurrentUser().equals(updatedPost.getUser())) {
            if(post.getTitle() != null){
                updatedPost.setTitle(post.getTitle());
            } else updatedPost.setTitle(updatedPost.getTitle());
            if(post.getContent() != null){
                updatedPost.setContent(post.getContent());
            }
            else updatedPost.setContent(updatedPost.getContent());
            postRepository.save(updatedPost);
            return convertPostToDTO(updatedPost);
        }
        else {
            log.error(messageConfig.getMessage(POST_NOT_FOUND));
            throw new UnauthorizedException(messageConfig.getMessage(ACCESS_DENIED));
        }
    }

    @Override
    public void deletePost(Long id) {
        log.info("Deleting post with {} from database", id);
        if(postRepository.existsById(id)) {
            Post postDeleted = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(messageConfig.getMessage(POST_NOT_FOUND, id)));
            User currentUser = userService.getCurrentUser();
            User user = postDeleted.getUser();
            if (currentUser.getRole().getRoleName().equals(RoleType.ADMIN) ||
                    currentUser.equals(postDeleted.getUser())) {
                List<Comment> deletedComments = commentRepository.findAllByPost_PostId(id);
                commentRepository.deleteAll(deletedComments);
                user.getPosts().remove(postDeleted);
                userRepository.save(user);
                postRepository.deleteById(id);
            }
        }
        else {
            log.error(messageConfig.getMessage(POST_NOT_FOUND));
            throw new ResourceNotFoundException(messageConfig.getMessage(POST_NOT_FOUND, id));
        }
    }

    @Override
    public void createPostWorkbook(HttpServletResponse response) throws IOException {
        log.info("Creating post workbook");
        List<Post> posts = postRepository.findTop5ByOrderByLikesCountDesc();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("TOP 5 POSTS");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("STT");
        header.createCell(1).setCellValue("Tiêu đề bài viết");
        header.createCell(2).setCellValue("Người đăng");
        header.createCell(3).setCellValue("Lượt thích");
        header.createCell(4).setCellValue("Lượt bình luận");
        int rowNum = 1; int x = 1;
        for(Post post : posts){
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(x++);
            row.createCell(1).setCellValue(post.getTitle());
            row.createCell(2).setCellValue(post.getUser().getUserName());
            row.createCell(3).setCellValue(post.getLikesCount());
            row.createCell(4).setCellValue(post.getComments().size());
        }
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"report.xlsx\"");
        //can co IOException
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
        log.info("Created post workbook");
    }

    @Override
    public PostListResponseDTO convertPostListToDTO(Post post) {
        PostListResponseDTO postListDTO = new PostListResponseDTO();
        postListDTO.setId(post.getPostId());
        postListDTO.setTitle(post.getTitle());
        postListDTO.setCreatedAt(post.getCreatedTime());
        postListDTO.setUpdatedAt(post.getUpdatedTime());
        postListDTO.setUserPost(post.getUser().getUserName());
        postListDTO.setCommentCount(post.getComments().size());
        postListDTO.setLikesCount(post.getLikesCount());
        postListDTO.setDislikesCount(post.getDislikesCount());
        return postListDTO;
    }
    @Override
    public PostResponseDTO convertPostToDTO(Post post) {
        PostResponseDTO postDTO = new PostResponseDTO();
        postDTO.setId(post.getPostId());
        postDTO.setTitle(post.getTitle());
        postDTO.setContent(post.getContent());
        postDTO.setUserPost(post.getUser().getUserName());
        postDTO.setCreatedAt(post.getCreatedTime());
        postDTO.setUpdatedAt(post.getUpdatedTime());
        if(post.getComments() == null){
            postDTO.setComments(new ArrayList<>());
        }
        else postDTO.setCommentCount(post.getComments().size());
        postDTO.setLikesCount(post.getLikesCount());
        postDTO.setDislikesCount(post.getDislikesCount());
        if(commentService.getCommentByPost(post.getPostId()) != null) {
            postDTO.setComments(commentService.getCommentByPost(post.getPostId()));
        }
        return postDTO;
    }

}
