package com.example.book.service.impl;

import com.example.book.config.MessageConfig;
import com.example.book.constant.RoleType;
import com.example.book.dto.RequestDTO.PostRequestDTO;
import com.example.book.dto.RequestDTO.Search.SearchPostRequest;
import com.example.book.dto.ResponseDTO.PageResponseDTO;
import com.example.book.dto.ResponseDTO.Post.PostListDTO;
import com.example.book.dto.ResponseDTO.Post.PostResponseDTO;
import com.example.book.entity.Post;
import com.example.book.exception.ResourceNotFoundException;
import com.example.book.exception.UnauthorizedException;
import com.example.book.repository.PostRepository;
import com.example.book.service.PostService;
import com.example.book.specification.PostSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserServiceImpl userServiceImpl;
    private final CommentServiceImpl commentServiceImpl;
    private final MessageConfig messageConfig;
    private final String POST_NOT_FOUND = "error.post.notfound";
    private final String ACCESS_DENIED = "error.auth.access.denied";

    public PostServiceImpl(PostRepository postRepository, UserServiceImpl userServiceImpl, CommentServiceImpl commentServiceImpl, MessageConfig messageConfig) {
        this.postRepository = postRepository;
        this.userServiceImpl = userServiceImpl;
        this.commentServiceImpl = commentServiceImpl;
        this.messageConfig = messageConfig;
    }

    @Override
    public PostResponseDTO addPost(PostRequestDTO request) {
        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setUser(userServiceImpl.getCurrentUser());
        postRepository.save(post);
        return convertPostToDTO(post);
    }

    @Override
    public PostResponseDTO getPost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(messageConfig.getMessage(POST_NOT_FOUND, id)));
        return convertPostToDTO(post);
    }

    @Override
    public PageResponseDTO<PostListDTO> getAllPosts(Pageable pageable) {
       Page<Post> posts = postRepository.findAll(pageable);
       Page<PostListDTO> postList = posts.map(this::convertPostListToDTO);
       return new PageResponseDTO<>(postList.getNumber(), postList.getNumberOfElements(), postList.getTotalPages(), postList.getContent());
    }

    @Override
    public PageResponseDTO<PostListDTO> searchPost(Pageable pageable, SearchPostRequest request){
        Specification<Post> spec = ((root, query, criteriaBuilder) -> criteriaBuilder.conjunction());
        String title = request.getTitle();
        String content = request.getContent();
        String userName = request.getUserName();
        LocalDate beforeDate = request.getBeforeDate();
        LocalDate afterDate = request.getAfterDate();
        if(StringUtils.hasText(title)){
            spec = spec.and(PostSpecification.likeTitle(title));
        }
        if(StringUtils.hasText(content)){
            spec = spec.and(PostSpecification.likeContent(content));
        }
        if(StringUtils.hasText(userName)){
            spec = spec.and(PostSpecification.hasUser(userName));
        }
        if(beforeDate != null){
            spec = spec.and(PostSpecification.uploadBeforeDate(beforeDate));
        }
        if(afterDate != null){
            spec = spec.and(PostSpecification.uploadAfterDate(afterDate));
        }
        Page<Post> posts = postRepository.findAll(spec, pageable);
        Page<PostListDTO> postList = posts.map(this::convertPostListToDTO);
        return new PageResponseDTO<>(postList.getNumber(), postList.getNumberOfElements(),
                postList.getTotalPages(), postList.getContent());
    }

    @Override
    public PostResponseDTO updatePost(Long id, PostRequestDTO post) throws UnauthorizedException {
        Post updatedPost = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(messageConfig.getMessage(POST_NOT_FOUND, id)));
        if(userServiceImpl.getCurrentUser().getRole().getRoleName().equals(RoleType.ADMIN) ||
                userServiceImpl.getCurrentUser().equals(updatedPost.getUser())) {
            updatedPost.setTitle(post.getTitle());
            updatedPost.setContent(post.getContent());
            postRepository.save(updatedPost);
            return convertPostToDTO(updatedPost);
        }
        else throw new UnauthorizedException(messageConfig.getMessage(ACCESS_DENIED));
    }

    @Override
    public void deletePost(Long id) {
        if(postRepository.existsById(id)) {
            postRepository.deleteById(id);
        }
        else throw new ResourceNotFoundException(messageConfig.getMessage(POST_NOT_FOUND, id));
    }

    public PostListDTO convertPostListToDTO(Post post) {
        PostListDTO postListDTO = new PostListDTO();
        postListDTO.setId(post.getPostId());
        postListDTO.setTitle(post.getTitle());
        postListDTO.setCreatedAt(post.getCreatedDate());
        postListDTO.setUpdatedAt(post.getLastModifiedDate());
        postListDTO.setUserPost(post.getUser().getUserName());
        postListDTO.setCommentCount(post.getComments().size());
        postListDTO.setLikesCount(post.getLikesCount());
        postListDTO.setDislikesCount(post.getDislikesCount());
        return postListDTO;
    }

    public PostResponseDTO convertPostToDTO(Post post) {
        PostResponseDTO postDTO = new PostResponseDTO();
        postDTO.setId(post.getPostId());
        postDTO.setTitle(post.getTitle());
        postDTO.setContent(post.getContent());
        postDTO.setUserPost(post.getUser().getUserName());
        postDTO.setCreatedAt(post.getCreatedDate());
        postDTO.setUpdatedAt(post.getLastModifiedDate());
        postDTO.setCommentCount(post.getComments().size());
        postDTO.setLikesCount(post.getLikesCount());
        postDTO.setDislikesCount(post.getDislikesCount());
        if(commentServiceImpl.getCommentByPost(post.getPostId()) != null) {
            postDTO.setComments(commentServiceImpl.getCommentByPost(post.getPostId()));
        }
        return postDTO;
    }
}
