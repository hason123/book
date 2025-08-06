package com.example.book.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.aspectj.bridge.IMessage;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="user")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;
    @NotEmpty
    @Column(unique = true)
    private String userName;
    private String password;
    @NotEmpty
    @Column(unique = true)
    private String fullName;
    @NotEmpty
    @Column(unique = true, length = 10)
    private String phoneNumber;
    @Column(unique = true, length = 12)
    private String identityNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthday;
    private String address;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String refreshToken;
    @ManyToOne
    @JoinColumn(name = "user_role_id")
    private Role role;
    @OneToMany(orphanRemoval = true, mappedBy = "user")
    private List<Post> posts;
    @OneToMany(orphanRemoval = true, mappedBy = "user")
    private List<Comment> comments;
    @OneToMany(mappedBy = "user")
    private List<Borrowing> borrowing;
    @OneToMany(orphanRemoval = true, mappedBy = "user")
    private List<PostReaction> postReactions;




}
