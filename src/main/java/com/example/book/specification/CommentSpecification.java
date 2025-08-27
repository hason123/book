package com.example.book.specification;

import com.example.book.entity.Comment;
import com.example.book.entity.User;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

    public static Specification<Comment> uploadBeforeDate(LocalDate uploadDate) {
        LocalDateTime endOfDay = uploadDate.atTime(23, 59, 59, 999_999_999);
        return (root,query, cb)
                -> cb.lessThanOrEqualTo(root.get("createdTime"), endOfDay);
    }

    public static Specification<Comment> uploadAfterDate(LocalDate uploadDate) {
        LocalDateTime startOfDay = uploadDate.atStartOfDay();
        return (root,query, cb)
                -> cb.greaterThanOrEqualTo(root.get("createdTime"), startOfDay);
    }

}
