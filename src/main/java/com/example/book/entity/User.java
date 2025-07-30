package com.example.book.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

    private String userName;
    private String password;
    private String fullName;
    private String phoneNumber;
    private String identityNumber;
    // private int age;
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


}
