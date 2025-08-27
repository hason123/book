package com.example.book.controller;

import com.example.book.dto.RequestDTO.PostRequestDTO;
import com.example.book.dto.RequestDTO.Search.SearchPostRequest;
import com.example.book.dto.ResponseDTO.Comment.CommentResponseDTO;
import com.example.book.dto.ResponseDTO.PageResponseDTO;
import com.example.book.dto.ResponseDTO.Post.PostListResponseDTO;
import com.example.book.dto.ResponseDTO.Post.PostResponseDTO;
import com.example.book.exception.UnauthorizedException;
import com.example.book.service.CommentService;
import com.example.book.service.PostReactionService;;
import com.example.book.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/library")
public class PostController {
    private final PostService postService;
    private final CommentService commentService;
    private final PostReactionService postReactionService;

    public PostController(PostService postService, CommentService commentService, PostReactionService postReactionService) {
        this.postService = postService;
        this.commentService = commentService;
        this.postReactionService = postReactionService;
    }

    @Operation(summary = "")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/posts")
    public ResponseEntity<PageResponseDTO<PostListResponseDTO>> getAllPosts(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
                                                                            @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        PageResponseDTO<PostListResponseDTO> posts = postService.getAllPosts(pageable);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @Operation(summary = "Lấy thông tin bài viết")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/posts/{id}")
    public ResponseEntity<PostResponseDTO> getPostById(@PathVariable long id) {
        PostResponseDTO post = postService.getPost(id);
        return ResponseEntity.ok(post);
    }

    @Operation(summary = "Thêm mới bài viết")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/posts")
    public ResponseEntity<PostResponseDTO> createPost(@RequestBody PostRequestDTO post){
        PostResponseDTO postCreate = postService.addPost(post);
        return new ResponseEntity<>(postCreate, HttpStatus.CREATED);
    }

    @Operation(summary = "Cập nhật bài viết")
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/posts/{id}")
    public ResponseEntity<PostResponseDTO> updatePost(@PathVariable long id, @RequestBody PostRequestDTO post) throws UnauthorizedException {
        PostResponseDTO postUpdate = postService.updatePost(id, post);
        return new ResponseEntity<>(postUpdate, HttpStatus.OK);
    }

    @Operation(summary = "Xóa bài viết")
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<String> deletePost(@PathVariable long id){
        postService.deletePost(id);
        return ResponseEntity.status(200).body("Delete successful");
    }

    @Operation(summary = "Lấy danh sách bình luận của một bài viết cụ thể")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentResponseDTO>> getCommentsByPostId(@PathVariable long postId) {
        List<CommentResponseDTO> commentPosts =  commentService.getCommentByPost(postId);
        return ResponseEntity.ok(commentPosts);
    }

    @Operation(summary = "Tìm kiếm bài viết")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/posts/search")
    public ResponseEntity<PageResponseDTO<PostListResponseDTO>> searchPost(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
                                                                           @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize, SearchPostRequest request) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        PageResponseDTO<PostListResponseDTO> posts = postService.searchPost(pageable, request);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @Operation(summary = "Thống kê Top 5 bài viết được yêu thích nhất")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/posts/dashboard")
    public ResponseEntity<?> getPostDashboard(HttpServletResponse response) throws IOException {
        postService.createPostWorkbook(response);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Thích bài viết")
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/posts/{id}/like")
    public ResponseEntity<?> likePost(@PathVariable Long id){
        postReactionService.likePost(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Không thích bài viết")
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/posts/{id}/dislike")
    public ResponseEntity<?> dislikePost(@PathVariable Long id){
        postReactionService.disLikePost(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
