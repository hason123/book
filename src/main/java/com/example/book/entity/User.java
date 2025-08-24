package com.example.book.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Where;
import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name= "users")
@SQLDelete(sql = "UPDATE users SET is_deleted = true WHERE user_id = ?")
@SQLRestriction(value = "is_deleted = false") //mac dinh chi lay nhung ban gi ko bi soft delete
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "user_name", unique = true)
    private String userName;
    @Column(name = "pass_word")
    private String password;
    @Column(name = "full_name", unique = true)
    private String fullName;
    @NotEmpty
    @Column(name = "phone_number", unique = true)
    private String phoneNumber;
    @Column(name = "identity_number", unique = true)
    private String identityNumber;
    @Column(name = "birthday")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthday;
    @Column(name = "address")
    private String address;
    @Column(name = "refresh_token", columnDefinition = "MEDIUMTEXT")
    private String refreshToken;
    @ManyToOne
    @JoinColumn(name = "user_role_id")
    private Role role;
    @OneToMany(mappedBy = "user")
    private List<Post> posts;
    @OneToMany(mappedBy = "user")
    private List<Comment> comments;
    @OneToMany(mappedBy = "user")
    private List<Borrowing> borrowing;
    @OneToMany(mappedBy = "user")
    private List<PostReaction> postReactions;

}
