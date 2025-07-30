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
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;
    private String title;
    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Instant createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Instant updatedAt;

    //phai anh xa sang user
    @ManyToOne
    @JoinColumn(name = "user_post_id")
    private User user;

    @OneToMany( orphanRemoval = true,mappedBy = "post")
    private List<Comment> comments;

    @PrePersist
    public void handleOnCreate() {
        createdAt = Instant.now();
    }

    @PreUpdate
    public void handleOnUpdate() {
        updatedAt = Instant.now();
    }



}
