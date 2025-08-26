package com.example.book.repository;

import com.example.book.entity.PostReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostReactionRepository extends JpaRepository<PostReaction, Long> {

    PostReaction findByUser_UserIdAndPost_PostId(Long userId, Long postId);

    List<PostReaction> findByUser_UserId(Long userId);

}
