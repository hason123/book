package com.example.book.service;

import com.example.book.dto.ResponseDTO.Post.PostCreateResponseDTO;
import com.example.book.dto.ResponseDTO.Post.PostListResponseDTO;
import com.example.book.dto.ResponseDTO.Post.PostResponseDTO;
import com.example.book.dto.ResponseDTO.Post.PostUpdateResponseDTO;
import com.example.book.entity.Post;

import java.util.List;
import java.util.Optional;


public interface PostService {

    PostCreateResponseDTO addPost(Post post);

    PostUpdateResponseDTO updatePost(Long id, Post post);

    Optional<Post> getPost(Long id);

    List<PostListResponseDTO> getAllPosts();

    void deletePost(Long id);





}
