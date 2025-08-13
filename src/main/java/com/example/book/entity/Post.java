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
    @OneToMany(mappedBy = "post")
    private List<Comment> comments;
    @OneToMany(mappedBy = "post")
    private List<PostReaction> postReactions;
    @Column(nullable = false, columnDefinition = "mediumint default 0")
    private int likesCount;
    @Column(nullable = false, columnDefinition = "mediumint default 0")
    private int dislikesCount;

    public void setLikesCount(int likesCount) {
        this.likesCount = Math.max(likesCount, 0);
    }

    public void setDislikesCount(int dislikesCount) {
        this.dislikesCount = Math.max(dislikesCount, 0);
    }





}
