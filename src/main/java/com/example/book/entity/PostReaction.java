package com.example.book.entity;

import com.example.book.constant.ReactionType;
import com.example.book.constant.RoleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PostReaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postReactionId;

    @ManyToOne
    @JoinColumn(name = "react_user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "react_post_id")
    private Post post;

    @Enumerated(EnumType.STRING) //luu data vao DB dang String
    private ReactionType reactionType;
}
