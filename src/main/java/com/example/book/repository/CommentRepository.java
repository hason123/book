package com.example.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import com.example.book.entity.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {
    List<Comment> findAllByPost_PostId(Long postId);

    List<Comment> findAllByUser_UserId(Long userId);

    void deleteAllByParent_CommentId(Long parentCommentId);

    List<Comment> findAllByParent_CommentId(Long parentCommentId);







}
