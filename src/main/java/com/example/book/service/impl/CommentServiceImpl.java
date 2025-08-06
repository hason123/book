package com.example.book.service.impl;

import com.example.book.dto.ResponseDTO.Comment.CommentDTO;
import com.example.book.dto.ResponseDTO.Comment.CommentResponseDTO;
import com.example.book.dto.ResponseDTO.UserCommentPostDTO;
import com.example.book.entity.Comment;
import com.example.book.repository.CommentRepository;
import com.example.book.repository.PostRepository;
import com.example.book.repository.UserRepository;
import com.example.book.service.CommentService;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public CommentServiceImpl(CommentRepository commentRepository, UserRepository userRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @Override
    public CommentDTO addComment(Comment comment) {
        if(!userRepository.existsById(comment.getUser().getUserId()) ||
                !postRepository.existsById(comment.getPost().getPostId())) {
            throw new IllegalStateException("User or Post doesn't exist");
        }
        commentRepository.save(comment);
        return convertCommentDTO(comment);
    }

    @Override
    public CommentDTO updateComment(Long id, Comment comment) {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        if (optionalComment.isPresent()) {
            Comment updatedComment = optionalComment.get();
            updatedComment.setCommentDetail(comment.getCommentDetail());
            updatedComment.setUpdatedAt(comment.getUpdatedAt());
            commentRepository.save(updatedComment);
            return convertCommentUpdateDTO(updatedComment);
        } else {
            throw new EntityNotFoundException("Post not found with id: " + id);
        }
    }

    @Override
    public List<CommentResponseDTO> getComments() {
        List<Comment> comments = commentRepository.findAll();
        List<CommentResponseDTO> commentResponseDTOS = new ArrayList<>();
        for(Comment comment : comments) {
            CommentResponseDTO commentResponseDTO = convertCommentToDTO(comment);
            commentResponseDTOS.add(commentResponseDTO);
        }
        return commentResponseDTOS;
    }

    @Override
    public CommentResponseDTO getComment(Long id) {
        Optional<Comment> commentOptional = commentRepository.findById(id);
        if(commentOptional.isPresent()) {
            Comment comment = commentOptional.get();
            return convertCommentToDTO(comment);
        }
        return null;
    }



    @Override
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    public CommentDTO convertCommentDTO(Comment comment){
        CommentDTO commentCreate = new CommentDTO();
        commentCreate.setCommentDetail(comment.getCommentDetail());
        commentCreate.setCommentId(comment.getCommentId());
        commentCreate.setCreatedAt(comment.getCreatedAt());
        UserCommentPostDTO userComment = new UserCommentPostDTO();
        userComment.setUserName(comment.getUser().getUserName());
        userComment.setUserId(comment.getUser().getUserId());
        commentCreate.setUserComment(userComment);
        return commentCreate;
    }

    public CommentDTO convertCommentUpdateDTO(Comment comment){
        CommentDTO commentUpdate = new CommentDTO();
        commentUpdate.setCommentDetail(comment.getCommentDetail());
        commentUpdate.setCommentId(comment.getCommentId());
        commentUpdate.setUpdatedAt(comment.getUpdatedAt());
        UserCommentPostDTO userComment = new UserCommentPostDTO();
        userComment.setUserName(comment.getUser().getUserName());
        userComment.setUserId(comment.getUser().getUserId());
        commentUpdate.setUserComment(userComment);
        return commentUpdate;
    }

    public CommentResponseDTO convertCommentToDTO(Comment comment){
        CommentResponseDTO commentResponse = getCommentResponseDTO(comment);
        CommentResponseDTO.PostDTO postDTO = new CommentResponseDTO.PostDTO();
        postDTO.setPostTitle(comment.getPost().getTitle());
        postDTO.setPostId(comment.getPost().getPostId());
        UserCommentPostDTO userPost = new UserCommentPostDTO();
        userPost.setUserName(comment.getPost().getUser().getUserName());
        userPost.setUserId(comment.getPost().getUser().getUserId());
        postDTO.setUserPost(userPost);
        return commentResponse;
    }

    private static CommentResponseDTO getCommentResponseDTO(Comment comment) {
        CommentResponseDTO commentResponse = new CommentResponseDTO();
        commentResponse.setCreatedAt(comment.getCreatedAt());
        commentResponse.setCommentDetail(comment.getCommentDetail());
        commentResponse.setUpdatedAt(comment.getUpdatedAt());
        UserCommentPostDTO userComment = new UserCommentPostDTO();
        userComment.setUserName(comment.getUser().getUserName());
        userComment.setUserId(comment.getUser().getUserId());
        commentResponse.setUserComment(userComment);
        return commentResponse;
    }
}
