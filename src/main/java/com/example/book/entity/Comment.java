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


}
