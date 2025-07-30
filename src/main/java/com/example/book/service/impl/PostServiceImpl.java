package com.example.book.service.impl;


import com.example.book.dto.ResponseDTO.Comment.CommentUpdateResponseDTO;
import com.example.book.dto.ResponseDTO.Post.PostCreateResponseDTO;
import com.example.book.dto.ResponseDTO.Post.PostListResponseDTO;
import com.example.book.dto.ResponseDTO.Post.PostResponseDTO;
import com.example.book.dto.ResponseDTO.Post.PostUpdateResponseDTO;
import com.example.book.dto.ResponseDTO.UserCommentPostDTO;
import com.example.book.entity.Comment;
import com.example.book.entity.Post;
import com.example.book.entity.User;
import com.example.book.repository.PostRepository;
import com.example.book.repository.UserRepository;
import com.example.book.service.PostService;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.Response;
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
    public PostCreateResponseDTO addPost(Post post) {
        if(!userRepository.existsById(post.getUser().getUserId())){
            throw new EntityNotFoundException("User not found");
        }
        postRepository.save(post);
        return convertPostCreateToDTO(post);
    }

    /*
    @Override
    public PostUpdateResponseDTO updatePost(Long id, Post post) {
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
    public PostUpdateResponseDTO updatePost(Long id, Post post) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isPresent()) {
            Post updatedPost = optionalPost.get();
            updatedPost.setTitle(post.getTitle());
            updatedPost.setContent(post.getContent());
            updatedPost.setUpdatedAt(post.getUpdatedAt());
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
    public List<PostListResponseDTO> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        List<PostListResponseDTO> postList = new ArrayList<>();
        for (Post post : posts) {
            PostListResponseDTO postDTO = convertPostListToDTO(post);
            postList.add(postDTO);
        }
        return postList;
    }

    @Override
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    public PostListResponseDTO convertPostListToDTO(Post post) {
        PostListResponseDTO postListDTO = new PostListResponseDTO();
        postListDTO.setId(post.getPostId());
        postListDTO.setTitle(post.getTitle());
        postListDTO.setCreatedAt(post.getCreatedAt());
        postListDTO.setUpdatedAt(post.getUpdatedAt());
        UserCommentPostDTO userPost = new UserCommentPostDTO();
        userPost.setUserId(post.getUser().getUserId());
        userPost.setUserName(post.getUser().getUserName());
        postListDTO.setUserPost(userPost);
        postListDTO.setCommentCount(post.getComments().size());
        return postListDTO;
    }

    public PostCreateResponseDTO convertPostCreateToDTO(Post post) {
        PostCreateResponseDTO postCreateResponseDTO = new PostCreateResponseDTO();
        postCreateResponseDTO.setId(post.getPostId());
        postCreateResponseDTO.setTitle(post.getTitle());
        postCreateResponseDTO.setCreatedAt(post.getCreatedAt());
        postCreateResponseDTO.setContent(post.getContent());
        UserCommentPostDTO userPost = new UserCommentPostDTO();
        userPost.setUserId(post.getUser().getUserId());
        userPost.setUserName(post.getUser().getUserName());
        postCreateResponseDTO.setUserPost(userPost);
        return postCreateResponseDTO;
    }

    public PostUpdateResponseDTO convertPostUpdateToDTO(Post post) {
        PostUpdateResponseDTO postUpdateResponseDTO = new PostUpdateResponseDTO();
        postUpdateResponseDTO.setId(post.getPostId());
        postUpdateResponseDTO.setTitle(post.getTitle());
        postUpdateResponseDTO.setContent(post.getContent());
        postUpdateResponseDTO.setUpdatedAt(post.getUpdatedAt());
        UserCommentPostDTO userPost = new UserCommentPostDTO();
        userPost.setUserId(post.getUser().getUserId());
        userPost.setUserName(post.getUser().getUserName());
        postUpdateResponseDTO.setUserPost(userPost);
        return postUpdateResponseDTO;
    }

    public PostResponseDTO convertPostResponseToDTO(Post post) {
        PostResponseDTO postResponseDTO = new PostResponseDTO();
        postResponseDTO.setId(post.getPostId());
        postResponseDTO.setTitle(post.getTitle());
        postResponseDTO.setContent(post.getContent());
        UserCommentPostDTO userPost = new UserCommentPostDTO();
        userPost.setUserId(post.getUser().getUserId());
        userPost.setUserName(post.getUser().getUserName());
        postResponseDTO.setUserPost(userPost);
        List<CommentUpdateResponseDTO> commentUpdateDTO = new ArrayList<>();
        for(Comment comment : post.getComments()) {
            CommentUpdateResponseDTO commentUpdate = new CommentUpdateResponseDTO();
            commentUpdate.setCommentDetail(comment.getCommentDetail());
            //commentUpdate.setCommentId(comment.getCommentId());
            commentUpdate.setUpdatedAt(comment.getUpdatedAt());
            UserCommentPostDTO userComment = new UserCommentPostDTO();
           // userPost.setUserId(comment.getUser().getUserId());
            userPost.setUserName(comment.getUser().getUserName());
            commentUpdate.setUserComment(userComment);
            commentUpdateDTO.add(commentUpdate);
        }
        postResponseDTO.setComments(commentUpdateDTO);
        return postResponseDTO;
    }



}
