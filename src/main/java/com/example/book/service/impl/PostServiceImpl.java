package com.example.book.service.impl;

import com.example.book.config.MessageConfig;
import com.example.book.constant.RoleType;
import com.example.book.dto.RequestDTO.PostRequestDTO;
import com.example.book.dto.RequestDTO.Search.SearchPostRequest;
import com.example.book.dto.ResponseDTO.PageResponseDTO;
import com.example.book.dto.ResponseDTO.Post.PostListDTO;
import com.example.book.dto.ResponseDTO.Post.PostResponseDTO;
import com.example.book.entity.Post;
import com.example.book.exception.ResourceNotFoundException;
import com.example.book.exception.UnauthorizedException;
import com.example.book.repository.PostRepository;
import com.example.book.service.PostService;
import com.example.book.specification.PostSpecification;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserServiceImpl userServiceImpl;
    private final CommentServiceImpl commentServiceImpl;
    private final MessageConfig messageConfig;
    private final String POST_NOT_FOUND = "error.post.notfound";
    private final String ACCESS_DENIED = "error.auth.accessDenied";

    public PostServiceImpl(PostRepository postRepository, UserServiceImpl userServiceImpl, CommentServiceImpl commentServiceImpl, MessageConfig messageConfig) {
        this.postRepository = postRepository;
        this.userServiceImpl = userServiceImpl;
        this.commentServiceImpl = commentServiceImpl;
        this.messageConfig = messageConfig;
    }

    @Override
    public PostResponseDTO addPost(PostRequestDTO request) {
        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setUser(userServiceImpl.getCurrentUser());
        postRepository.save(post);
        return convertPostToDTO(post);
    }

    @Override
    public PostResponseDTO getPost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(messageConfig.getMessage(POST_NOT_FOUND, id)));
        return convertPostToDTO(post);
    }

    @Override
    public PageResponseDTO<PostListDTO> getAllPosts(Pageable pageable) {
       Page<Post> posts = postRepository.findAll(pageable);
       Page<PostListDTO> postList = posts.map(this::convertPostListToDTO);
       return new PageResponseDTO<>(postList.getNumber(), postList.getNumberOfElements(), postList.getTotalPages(), postList.getContent());
    }

    @Override
    public PageResponseDTO<PostListDTO> searchPost(Pageable pageable, SearchPostRequest request){
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
        Page<PostListDTO> postList = posts.map(this::convertPostListToDTO);
        return new PageResponseDTO<>(postList.getNumber(), postList.getNumberOfElements(),
                postList.getTotalPages(), postList.getContent());
    }

    @Override
    public PostResponseDTO updatePost(Long id, PostRequestDTO post) throws UnauthorizedException {
        Post updatedPost = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(messageConfig.getMessage(POST_NOT_FOUND, id)));
        if(userServiceImpl.getCurrentUser().getRole().getRoleName().equals(RoleType.ADMIN) ||
                userServiceImpl.getCurrentUser().equals(updatedPost.getUser())) {
            updatedPost.setTitle(post.getTitle());
            updatedPost.setContent(post.getContent());
            postRepository.save(updatedPost);
            return convertPostToDTO(updatedPost);
        }
        else throw new UnauthorizedException(messageConfig.getMessage(ACCESS_DENIED));
    }

    @Override
    public void deletePost(Long id) {
        if(postRepository.existsById(id)) {
            postRepository.deleteById(id);
        }
        else throw new ResourceNotFoundException(messageConfig.getMessage(POST_NOT_FOUND, id));
    }

    @Override
    @Scheduled(cron = "0 0 0 * * *")
    public void createPostWorkbook(HttpServletResponse response) throws IOException {
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

    }

    public PostListDTO convertPostListToDTO(Post post) {
        PostListDTO postListDTO = new PostListDTO();
        postListDTO.setId(post.getPostId());
        postListDTO.setTitle(post.getTitle());
        postListDTO.setCreatedAt(post.getCreatedDate());
        postListDTO.setUpdatedAt(post.getLastModifiedDate());
        postListDTO.setUserPost(post.getUser().getUserName());
        postListDTO.setCommentCount(post.getComments().size());
        postListDTO.setLikesCount(post.getLikesCount());
        postListDTO.setDislikesCount(post.getDislikesCount());
        return postListDTO;
    }

    public PostResponseDTO convertPostToDTO(Post post) {
        PostResponseDTO postDTO = new PostResponseDTO();
        postDTO.setId(post.getPostId());
        postDTO.setTitle(post.getTitle());
        postDTO.setContent(post.getContent());
        postDTO.setUserPost(post.getUser().getUserName());
        postDTO.setCreatedAt(post.getCreatedDate());
        postDTO.setUpdatedAt(post.getLastModifiedDate());
        postDTO.setCommentCount(post.getComments().size());
        postDTO.setLikesCount(post.getLikesCount());
        postDTO.setDislikesCount(post.getDislikesCount());
        if(commentServiceImpl.getCommentByPost(post.getPostId()) != null) {
            postDTO.setComments(commentServiceImpl.getCommentByPost(post.getPostId()));
        }
        return postDTO;
    }
}
