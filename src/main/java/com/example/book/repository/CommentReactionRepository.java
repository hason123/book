package com.example.book.repository;

import com.example.book.entity.CommentReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentReactionRepository extends JpaRepository<CommentReaction, Long> {
    CommentReaction findByUser_UserIdAndComment_CommentId(Long userId, Long commentId);

    List<CommentReaction> findByUser_UserId(Long userId);
}
