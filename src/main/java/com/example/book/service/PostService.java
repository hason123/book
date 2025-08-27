package com.example.book.service;

import com.example.book.dto.RequestDTO.PostRequestDTO;
import com.example.book.dto.RequestDTO.Search.SearchPostRequest;
import com.example.book.dto.ResponseDTO.PageResponseDTO;
import com.example.book.dto.ResponseDTO.Post.PostListResponseDTO;
import com.example.book.dto.ResponseDTO.Post.PostResponseDTO;
import com.example.book.entity.Post;
import com.example.book.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Pageable;
import java.io.IOException;

public interface PostService {

    PostResponseDTO addPost(PostRequestDTO request);

    PostResponseDTO getPost(Long id);

    PageResponseDTO<PostListResponseDTO> getAllPosts(Pageable pageable);

    PageResponseDTO<PostListResponseDTO> searchPost(Pageable pageable, SearchPostRequest request);

    PostResponseDTO updatePost(Long id, PostRequestDTO post) throws UnauthorizedException;

    void deletePost(Long id);

    void createPostWorkbook(HttpServletResponse response) throws IOException;

    PostListResponseDTO convertPostListToDTO(Post post);

    PostResponseDTO convertPostToDTO(Post post);
}
