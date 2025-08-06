package com.example.book.controller;


import com.example.book.dto.ResponseDTO.Post.PostListDTO;
import com.example.book.dto.ResponseDTO.Post.PostDTO;
import com.example.book.entity.Post;
import com.example.book.service.impl.PostServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/library")
public class PostController {
    private final PostServiceImpl postServiceImpl;


    public PostController(PostServiceImpl postServiceImpl) {
        this.postServiceImpl = postServiceImpl;

    }

    @GetMapping("/posts")
    public ResponseEntity<List<PostListDTO>> getAllPosts() {
        List<PostListDTO> posts = postServiceImpl.getAllPosts();
        if(posts.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping("/post/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable long id) {
        Optional<Post> post = postServiceImpl.getPost(id);
        if(post.isPresent()) {
            PostDTO postDTO = postServiceImpl.convertPostResponseToDTO(post.get());
            return new ResponseEntity<>(postDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/post/create")
    public ResponseEntity<PostDTO> createPost(@RequestBody Post post){
        PostDTO postCreate = postServiceImpl.convertPostCreateToDTO(post);
        return new ResponseEntity<>(postCreate, HttpStatus.CREATED);
    }

    @PutMapping("/post/update/{id}")
    public ResponseEntity<PostDTO> updatePost(@PathVariable long id, @RequestBody Post post){
        PostDTO postUpdate = postServiceImpl.updatePost(id, post);
        if(postUpdate == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(postUpdate, HttpStatus.OK);
    }

    @DeleteMapping("/post/delete/{id}")
    public ResponseEntity<String> deletePost(@PathVariable long id){
        postServiceImpl.deletePost(id);
        return ResponseEntity.status(200).body("Delete successful");

    }
}
