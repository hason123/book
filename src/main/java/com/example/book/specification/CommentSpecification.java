package com.example.book.specification;

import com.example.book.entity.Comment;
import com.example.book.entity.User;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class CommentSpecification {
    public static Specification<Comment> likeContent(String content) {
        return (root,query, cb)
                -> cb.like(root.get("content"), "%" + content.toLowerCase() + "%");
    }

    public static Specification<Comment> hasUser(String userName) {
        return (root,query,cb) -> {
            Join<Comment, User> postUser = root.join("user");
            return cb.equal(postUser.get("userName"), userName);
        };
    }
}
