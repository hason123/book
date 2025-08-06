package com.example.book.service;

import com.example.book.dto.ResponseDTO.Post.PostDTO;
import com.example.book.dto.ResponseDTO.Post.PostListDTO;
import com.example.book.entity.Post;

import java.util.List;
import java.util.Optional;


public interface PostService {

    PostDTO addPost(Post post);

    PostDTO updatePost(Long id, Post post);

    Optional<Post> getPost(Long id);

    List<PostListDTO> getAllPosts();

    void deletePost(Long id);





}
