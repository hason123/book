package com.example.book.entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;
    private String commentDetail;

    @ManyToOne
    @JoinColumn(name = "user_comment_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_comment_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Comment> children = new ArrayList<>();
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
