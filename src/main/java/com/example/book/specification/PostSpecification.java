package com.example.book.specification;

import com.example.book.entity.Post;
import com.example.book.entity.User;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;

public class PostSpecification {
    public static Specification<Post> likeTitle(String name) {
        return (root, query, cb)
                -> cb.like(root.get("title"), "%" + name.toLowerCase() + "%");
    }
    public static Specification<Post> likeContent(String content) {
        return (root,query, cb)
                -> cb.like(root.get("content"), "%" + content.toLowerCase() + "%");
    }
    public static Specification<Post> hasUser(String userName) {
        return (root,query,cb) -> {
            Join<Post, User> postUser = root.join("user");
            return cb.equal(postUser.get("userName"), userName);
        };
    }
    public static Specification<Post> uploadBeforeDate(LocalDate uploadDate) {
        return (root,query, cb)
                -> cb.lessThanOrEqualTo(root.get("createdDate"), uploadDate);
    }
    public static Specification<Post> uploadAfterDate(LocalDate uploadDate) {
        return (root,query, cb)
                -> cb.greaterThanOrEqualTo(root.get("createdDate"), uploadDate);
    }
    //By default, Spring Boot (via Jackson) accepts LocalDate in ISO 8601 format:
    //"2025-08-13"
}
