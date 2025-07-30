package com.example.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.book.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {


}
