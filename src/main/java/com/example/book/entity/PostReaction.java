package com.example.book.entity;

import com.example.book.constant.ReactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "post_reaction")
public class PostReaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_react_id")
    private Long postReactionId;
    @Column(name = "reaction_type")
    @Enumerated(EnumType.STRING) //luu data vao DB dang String
    private ReactionType reactionType;
    @ManyToOne
    @JoinColumn(name = "react_user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "react_post_id")
    private Post post;

}
