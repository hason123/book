package com.example.book.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;
import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SQLRestriction(value = "is_deleted = false") //mac dinh chi lay nhung ban gi ko bi soft delete
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
    @Column(unique = true)
    @Size(min = 10, max = 10, message = "Phone number must be exactly 10 digits")
    private String phoneNumber;
    @Column(unique = true)
    @Size(min = 12, max = 12, message = "Social Security must be exactly 12 digits")
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
