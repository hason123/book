package com.example.book.repository;

import com.example.book.entity.PostReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostReactionRepository extends JpaRepository<PostReaction, Long> {

    Boolean existsByUser_UserIdAndPost_PostId(Long userId, Long postId);

    PostReaction findByUser_UserIdAndPost_PostId(Long userId, Long postId);

}
