package com.example.book.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "post")
@SQLDelete(sql = "UPDATE post SET is_deleted = true WHERE post_id = ?")
@SQLRestriction(value = "is_deleted = false")
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;
    @Column(name = "title")
    private String title;
    @Column(name = "content")
    private String content;
    @Column(name = "created_time")
    private LocalDateTime createdTime;
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
    @Column(name = "likes_count", nullable = false, columnDefinition = "mediumint default 0")
    private int likesCount;
    @Column(name = "dislikes_count" , nullable = false, columnDefinition = "mediumint default 0")
    private int dislikesCount;
    @ManyToOne
    @JoinColumn(name = "user_post_id")
    private User user;
    @OneToMany(mappedBy = "post")
    private List<Comment> comments;
    @OneToMany(mappedBy = "post")
    private List<PostReaction> postReactions;

    public void setLikesCount(int likesCount) {
        this.likesCount = Math.max(likesCount, 0);
    }

    public void setDislikesCount(int dislikesCount) {
        this.dislikesCount = Math.max(dislikesCount, 0);
    }

    @PrePersist
    public void prePersist() {
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedTime = LocalDateTime.now();
    }

}
