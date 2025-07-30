package com.example.book.entity;


import jakarta.persistence.*;
import lombok.*;
import java.util.List;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @ManyToMany(mappedBy = "permissions")
    private List<Role> roles;
}
