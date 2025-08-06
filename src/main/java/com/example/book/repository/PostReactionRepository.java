package com.example.book.repository;

import com.example.book.entity.PostReaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostReactionRepository extends JpaRepository<PostReaction,Long> {
    Boolean existsByUserIdAndPostId(Long userId, Long postId);

    PostReaction findByUserIdAndPostId(Long userId, Long postId);





}
