package com.example.book.entity;


import com.example.book.constant.ReactionType;
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
public class CommentReaction extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentReactionId;

    @ManyToOne
    @JoinColumn(name = "react_user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "react_comment_id")
    private Comment comment;

    @Enumerated(EnumType.STRING) //luu data vao DB dang String
    private ReactionType reactionType;
}
