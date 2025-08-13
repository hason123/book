package com.example.book.service.impl;

import com.example.book.dto.ResponseDTO.Comment.CommentResponseDTO;
import com.example.book.entity.Comment;
import com.example.book.exception.ResourceNotFoundException;
import com.example.book.repository.CommentRepository;
import com.example.book.repository.PostRepository;
import com.example.book.repository.UserRepository;
import com.example.book.service.CommentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import java.util.*;

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
    public CommentResponseDTO addComment(Comment comment) {
        if(!userRepository.existsById(comment.getUser().getUserId()) ||
                !postRepository.existsById(comment.getPost().getPostId())) {
            throw new IllegalStateException("User or Post doesn't exist");
        }
        commentRepository.save(comment);
        return convertCommentToDTO(comment);
    }

    @Override
    public CommentResponseDTO updateComment(Long id, Comment comment) {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        if (optionalComment.isPresent()) {
            Comment updatedComment = optionalComment.get();
            updatedComment.setCommentDetail(comment.getCommentDetail());
            updatedComment.setLastModifiedDate(comment.getLastModifiedDate());
            commentRepository.save(updatedComment);
            return convertCommentToDTO(updatedComment);
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
    public List<CommentResponseDTO> getCommentByPost(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Post not found with id: " + postId);
        }
        List<Comment> comments = commentRepository.findAllByPost_PostId(postId);
        Map<Long, CommentResponseDTO> nodeMap = new HashMap<>();
        for (Comment c : comments) {
             CommentResponseDTO comment = new CommentResponseDTO();
             comment.setCommentDetail(c.getCommentDetail());
             comment.setCommentId(c.getCommentId());
             comment.setUserComment(c.getUser().getUserName());
            //  comment.setParentId(c.getParent().getCommentId() != null ? c.getParent().getCommentId() : null); // error NPE
             Comment parent = c.getParent();
             Long parentId = (parent != null) ? parent.getCommentId() : null;
             comment.setParentId(parentId);
             comment.setCreatedAt(c.getCreatedDate());
             comment.setUpdatedAt(c.getLastModifiedDate());
             comment.setReplies(new ArrayList<>());
             nodeMap.put(c.getCommentId(), comment);
        }

        List<CommentResponseDTO> commentRoots = new ArrayList<>();
        for(CommentResponseDTO commentNode : nodeMap.values()) {
            if(commentNode.getParentId() != null) {
                CommentResponseDTO comment = nodeMap.get(commentNode.getParentId());
                if (comment != null) {
                    comment.getReplies().add(commentNode);
                }
            }
            else{
                commentRoots.add(commentNode);
            }
        }
        //Comparator<CommentResponseDTO> comparator = Comparator.comparing(CommentResponseDTO::getCreatedAt);
        //commentRoots.sort(comparator.reversed());
        return commentRoots;

    }



    //@Override
    /*
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

     */
    //de the nay thi no se xoa het ca nhung comment con
    //co the can nhac de neu xoa comment nao thi ghi de ten nguoi dung la deleted, con noi dung comment la comment removed by user
    @Override
    public void deleteComment(Long id){
        commentRepository.deleteById(id);
    }

    public CommentResponseDTO convertCommentToDTO(Comment comment){
        CommentResponseDTO commentResponse = new CommentResponseDTO();
        commentResponse.setCreatedAt(comment.getCreatedDate());
        commentResponse.setCommentDetail(comment.getCommentDetail());
        commentResponse.setUpdatedAt(comment.getLastModifiedDate());
        commentResponse.setUserComment(comment.getUser().getUserName());
        if(comment.getParent() != null) {
            commentResponse.setParentId(comment.getParent().getCommentId());
        }
        commentResponse.setReplies(new ArrayList<>());
        List<CommentResponseDTO> commentChilds = new ArrayList<>();
        return commentResponse;
    }

    private static CommentResponseDTO getCommentResponseDTO(Comment comment) {
        CommentResponseDTO commentResponse = new CommentResponseDTO();
        commentResponse.setCreatedAt(comment.getCreatedDate());
        commentResponse.setCommentDetail(comment.getCommentDetail());
        commentResponse.setUpdatedAt(comment.getLastModifiedDate());
        commentResponse.setUserComment(comment.getUser().getUserName());
        return commentResponse;
    }


}

