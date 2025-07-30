package com.example.book.entity;



import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;




@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;
    private String commentDetail;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Instant createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Instant updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_comment_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_comment_id")
    private Post post;

    @PrePersist
    public void handleOnCreate() {
        createdAt = Instant.now();
    }

    @PreUpdate
    public void handleOnUpdate() {
        if(updatedAt == null) {
            updatedAt = createdAt;
        }
        updatedAt = Instant.now();
    }
}
