package com.example.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.book.entity.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPost_PostId(Long postId);

    void deleteAllByParent_CommentId(Long parentCommentId);

    List<Comment> findAllByParent_CommentId(Long parentCommentId);





}
