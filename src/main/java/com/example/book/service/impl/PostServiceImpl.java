package com.example.book.service.impl;


import com.example.book.dto.ResponseDTO.Comment.CommentDTO;
import com.example.book.dto.ResponseDTO.Post.PostListDTO;
import com.example.book.dto.ResponseDTO.Post.PostDTO;
import com.example.book.dto.ResponseDTO.UserCommentPostDTO;
import com.example.book.entity.Comment;
import com.example.book.entity.Post;
import com.example.book.repository.PostRepository;
import com.example.book.repository.UserRepository;
import com.example.book.service.PostService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    public PostDTO addPost(Post post) {
        if(!userRepository.existsById(post.getUser().getUserId())){
            throw new EntityNotFoundException("User not found");
        }
        postRepository.save(post);
        return convertPostCreateToDTO(post);
    }

    /*
    @Override
    public PostDTO updatePost(Long id, Post post) {
        Optional<Post> updatedPost = postRepository.findById(id);
        try{
            updatedPost.get().setTitle(post.getTitle());
            updatedPost.get().setContent(post.getContent());
            postRepository.save(updatedPost.get());
            return convertPostUpdateToDTO(updatedPost.get());
        }
        catch(EntityNotFoundException e){
            return null;
        }
    }
    */
    @Override
    public PostDTO updatePost(Long id, Post post) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isPresent()) {
            Post updatedPost = optionalPost.get();
            updatedPost.setTitle(post.getTitle());
            updatedPost.setContent(post.getContent());
            postRepository.save(updatedPost);
            return convertPostUpdateToDTO(updatedPost);
        } else {
            return null;
        }
    }


    @Override
    public Optional<Post> getPost(Long id) {
       return postRepository.findById(id);
    }

    @Override
    public List<PostListDTO> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        List<PostListDTO> postList = new ArrayList<>();
        for (Post post : posts) {
            PostListDTO postDTO = convertPostListToDTO(post);
            postList.add(postDTO);
        }
        return postList;
    }

    @Override
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    public PostListDTO convertPostListToDTO(Post post) {
        PostListDTO postListDTO = new PostListDTO();
        postListDTO.setId(post.getPostId());
        postListDTO.setTitle(post.getTitle());
       // postListDTO.setCreatedAt(post.getCreatedAt());
       // postListDTO.setUpdatedAt(post.getUpdatedAt());
        UserCommentPostDTO userPost = new UserCommentPostDTO();
        userPost.setUserId(post.getUser().getUserId());
        userPost.setUserName(post.getUser().getUserName());
        postListDTO.setUserPost(userPost);
        postListDTO.setCommentCount(post.getComments().size());
        return postListDTO;
    }

    public PostDTO convertPostCreateToDTO(Post post) {
        PostDTO PostDTO = new PostDTO();
        PostDTO.setId(post.getPostId());
        PostDTO.setTitle(post.getTitle());
       // PostDTO.setCreatedAt(post.getCreatedAt());
        PostDTO.setContent(post.getContent());
        UserCommentPostDTO userPost = new UserCommentPostDTO();
        userPost.setUserId(post.getUser().getUserId());
        userPost.setUserName(post.getUser().getUserName());
        PostDTO.setUserPost(userPost);
        return PostDTO;
    }

    public PostDTO convertPostUpdateToDTO(Post post) {
        PostDTO PostDTO = new PostDTO();
        PostDTO.setId(post.getPostId());
        PostDTO.setTitle(post.getTitle());
        PostDTO.setContent(post.getContent());
       // PostDTO.setUpdatedAt(post.getUpdatedAt());
        UserCommentPostDTO userPost = new UserCommentPostDTO();
        userPost.setUserId(post.getUser().getUserId());
        userPost.setUserName(post.getUser().getUserName());
        PostDTO.setUserPost(userPost);
        return PostDTO;
    }

    public PostDTO convertPostResponseToDTO(Post post) {
        PostDTO postDTO = new PostDTO();
        postDTO.setId(post.getPostId());
        postDTO.setTitle(post.getTitle());
        postDTO.setContent(post.getContent());
        UserCommentPostDTO userPost = new UserCommentPostDTO();
        userPost.setUserId(post.getUser().getUserId());
        userPost.setUserName(post.getUser().getUserName());
        postDTO.setUserPost(userPost);
        List<CommentDTO> commentUpdateDTO = new ArrayList<>();
        for(Comment comment : post.getComments()) {
            CommentDTO commentUpdate = new CommentDTO();
            commentUpdate.setCommentDetail(comment.getCommentDetail());
            //commentUpdate.setCommentId(comment.getCommentId());
            commentUpdate.setUpdatedAt(comment.getUpdatedAt());
            UserCommentPostDTO userComment = new UserCommentPostDTO();
           // userPost.setUserId(comment.getUser().getUserId());
            userPost.setUserName(comment.getUser().getUserName());
            commentUpdate.setUserComment(userComment);
            commentUpdateDTO.add(commentUpdate);
        }
        postDTO.setComments(commentUpdateDTO);
        return postDTO;
    }



}
