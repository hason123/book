package com.example.book.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;
    private String title;
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_post_id")
    private User user;

    @OneToMany( orphanRemoval = true,mappedBy = "post")
    private List<Comment> comments;

    @OneToMany(orphanRemoval = true, mappedBy = "post")
    private List<PostReaction> postReactions;

    private int likesCount;
    private int dislikesCount;
    //Override get set method
    public void setLikesCount(int likesCount) {
        this.likesCount = Math.max(likesCount, 0);
    }

    public void setDislikesCount(int dislikesCount) {
        this.dislikesCount = Math.max(dislikesCount, 0);
    }





}
