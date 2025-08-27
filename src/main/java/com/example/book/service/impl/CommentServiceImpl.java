package com.example.book.service.impl;

import com.example.book.config.MessageConfig;
import com.example.book.constant.RoleType;
import com.example.book.dto.RequestDTO.CommentRequestDTO;
import com.example.book.dto.RequestDTO.Search.SearchCommentRequest;
import com.example.book.dto.ResponseDTO.Comment.CommentResponseDTO;
import com.example.book.dto.ResponseDTO.Comment.CommentShortResponseDTO;
import com.example.book.dto.ResponseDTO.PageResponseDTO;
import com.example.book.entity.Comment;
import com.example.book.entity.Post;
import com.example.book.entity.User;
import com.example.book.exception.ResourceNotFoundException;
import com.example.book.repository.CommentRepository;
import com.example.book.repository.PostRepository;
import com.example.book.repository.UserRepository;
import com.example.book.service.CommentService;
import com.example.book.service.UserService;
import com.example.book.specification.CommentSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.*;
@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final MessageConfig messageConfig;
    private final UserService userService;
    private final String POST_NOT_FOUND = "error.post.notfound";
    private final String COMMENT_NOT_FOUND = "error.comment.notfound";

    public CommentServiceImpl(CommentRepository commentRepository, UserRepository userRepository, PostRepository postRepository, MessageConfig messageConfig, @Lazy UserService userService) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.messageConfig = messageConfig;
        this.userService = userService;
    }

    @Override
    public CommentShortResponseDTO addComment(Long postId, CommentRequestDTO request) {
        log.info("Add comment in post with id: {}", postId);
        if(!postRepository.existsById(postId)){
            log.error("Post with id: {} not found", postId);
            throw new ResourceNotFoundException(messageConfig.getMessage(POST_NOT_FOUND, postId));
        }
        Comment comment = new Comment();
        comment.setCommentDetail(request.getContent());
        commentRepository.save(comment);
        return convertCommentToShortDTO(comment);
    }

    @Override
    public CommentShortResponseDTO updateComment(Long postId, Long commentId, CommentRequestDTO request) {
        log.info("Update comment in post with id: {}", postId);
        if(!postRepository.existsById(postId)){
            log.error("Post with id: {} not found", postId);
            throw new ResourceNotFoundException(messageConfig.getMessage(POST_NOT_FOUND, postId));
        }
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isPresent()) {
            log.info("Updating comment in post with id: {}", postId);
            Comment updatedComment = optionalComment.get();
            if(request.getContent() != null){
                updatedComment.setCommentDetail(request.getContent());
            } else updatedComment.setCommentDetail(updatedComment.getCommentDetail());
            commentRepository.save(updatedComment);
            return convertCommentToShortDTO(updatedComment);
        } else {
            log.error("Comment with id: {} not found", commentId);
            throw new ResourceNotFoundException(messageConfig.getMessage(COMMENT_NOT_FOUND, commentId));
        }
    }

    @Override
    public PageResponseDTO<CommentShortResponseDTO> getComments(Pageable pageable) {
        log.info("Getting total comments!");
        Page<Comment> comments = commentRepository.findAll(pageable);
        Page<CommentShortResponseDTO> commentPage = comments.map(c -> convertCommentToShortDTO(c));
        log.info("Total comments: {}", commentPage.getTotalElements());
        return new PageResponseDTO<>(
                commentPage.getNumber() + 1,
                commentPage.getNumberOfElements(),
                commentPage.getTotalPages(),
                commentPage.getContent()
        );
    }

    @Override
    public CommentShortResponseDTO getComment(Long id) {
        log.info("Getting comment with id: {}", id);
        Optional<Comment> commentOptional = commentRepository.findById(id);
        if(commentOptional.isPresent()) {
            Comment comment = commentOptional.get();
            return convertCommentToShortDTO(comment);
        }
        else{
            log.info("Comment with id: {} not found", id);
            throw new ResourceNotFoundException(messageConfig.getMessage(COMMENT_NOT_FOUND, id));
        }
    }

    @Override
    public List<CommentResponseDTO> getCommentByPost(Long postId) {
        log.info("Getting comments by post with id: {}", postId);
        if (!postRepository.existsById(postId)) {
            log.error("Post with id: {} not found", postId);
            throw new ResourceNotFoundException(messageConfig.getMessage(POST_NOT_FOUND, postId));
        }
        List<Comment> comments = commentRepository.findAllByPost_PostId(postId);
        Map<Long, CommentResponseDTO> nodeMap = new HashMap<>();
        for (Comment c : comments) {
             CommentResponseDTO comment = convertCommentToDTO(c);
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
        Comparator<CommentResponseDTO> comparator = Comparator.comparing(CommentResponseDTO::getUpdatedAt);
        commentRoots.sort(comparator.reversed());
        commentRoots.forEach(root -> sortRepliesAscending(root.getReplies()));;
        log.info("Successfully build comment trees!");
        return commentRoots;
    }

    private void sortRepliesAscending(List<CommentResponseDTO> comments) {
        if (comments == null || comments.isEmpty()) return;
        comments.sort(Comparator.comparing(CommentResponseDTO::getUpdatedAt));
        for (CommentResponseDTO c : comments) {
            sortRepliesAscending(c.getReplies());
        }
    }

    @Override
    public void deleteComment(Long id){
        log.info("Deleting comment with id: {}", id);
        Comment commentDeleted = commentRepository.findById(id).orElseThrow(() ->
        {
            log.error("Comment with id: {} not found", id);
            return new ResourceNotFoundException(messageConfig.getMessage(COMMENT_NOT_FOUND, id));
        });
        Post post = commentDeleted.getPost();
        User user = commentDeleted.getUser();
        User currentUser = userService.getCurrentUser();
        if(currentUser.getRole().getRoleName().equals(RoleType.ADMIN) ||
                currentUser.equals(commentDeleted.getUser()) || currentUser.equals(commentDeleted.getPost().getUser())){
            List<Comment> comments = commentRepository.findAllByParent_CommentId(id);
            comments.forEach(c -> { if(commentDeleted.getParent() != null)
            {
                c.setParent(commentDeleted.getParent()); commentRepository.save(c);
            }
            else {c.setParent(null); commentRepository.save(c);}
            });
            post.getComments().remove(commentDeleted);
            postRepository.save(post);
            user.getComments().remove(commentDeleted);
            userRepository.save(user);
            commentRepository.delete(commentDeleted);
        }
        log.info("Comment with id: {} has been deleted", id);
    }

    @Override
    public PageResponseDTO<CommentShortResponseDTO> searchComment(Pageable pageable, SearchCommentRequest request){
        log.info("Searching posts from database");
        Specification<Comment> spec = ((root, query, criteriaBuilder) -> criteriaBuilder.conjunction());
        String content = request.getContent();
        String userName = request.getUserName();
        LocalDate beforeDate = request.getBeforeDate();
        LocalDate afterDate = request.getAfterDate();
        if(StringUtils.hasText(content)){
            spec = spec.and(CommentSpecification.likeContent(content));
        }
        if(StringUtils.hasText(userName)){
            spec = spec.and(CommentSpecification.hasUser(userName));
        }
        if(beforeDate != null){
            spec = spec.and(CommentSpecification.uploadBeforeDate(beforeDate));
        }
        if(afterDate != null){
            spec = spec.and(CommentSpecification.uploadAfterDate(afterDate));
        }
        Page<Comment> comments = commentRepository.findAll(spec, pageable);
        Page<CommentShortResponseDTO> commentPage = comments.map(this::convertCommentToShortDTO);
        log.info("Retrieved all posts from database");
        return new PageResponseDTO<>(commentPage.getNumber(), commentPage.getNumberOfElements(),
                commentPage.getTotalPages(), commentPage.getContent());
    }

    public CommentResponseDTO convertCommentToDTO(Comment comment){
        CommentResponseDTO commentResponse = new CommentResponseDTO();
        commentResponse.setCommentId(comment.getCommentId());
        commentResponse.setCreatedAt(comment.getCreatedTime());
        commentResponse.setCommentDetail(comment.getCommentDetail());
        commentResponse.setUpdatedAt(comment.getUpdatedTime());
        commentResponse.setUserComment(comment.getUser().getUserName());
        if(comment.getParent() != null) {
            commentResponse.setParentId(comment.getParent().getCommentId());
        }
        commentResponse.setReplies(new ArrayList<>());
        commentResponse.setLikes(comment.getLikesCount());
        commentResponse.setDislikes(comment.getDislikesCount());
        return commentResponse;
    }

    public CommentShortResponseDTO convertCommentToShortDTO(Comment comment){
        CommentShortResponseDTO commentResponse = new CommentShortResponseDTO();
        commentResponse.setCreatedAt(comment.getCreatedTime());
        commentResponse.setCommentDetail(comment.getCommentDetail());
        commentResponse.setUpdatedAt(comment.getUpdatedTime());
        commentResponse.setUserComment(comment.getUser().getUserName());
        commentResponse.setCommentLikes(comment.getLikesCount());
        commentResponse.setCommentId(comment.getCommentId());
        commentResponse.setCommentDislikes(comment.getDislikesCount());
        return commentResponse;
    }



}

