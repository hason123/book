package com.example.book.repository;

import com.example.book.entity.Comment;
import com.example.book.entity.CommentReaction;
import com.example.book.entity.PostReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentReactionRepository extends JpaRepository<CommentReaction, Long> {
    CommentReaction findByUser_UserIdAndComment_CommentId(Long userId, Long commentId);
}
