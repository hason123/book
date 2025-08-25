package com.example.book.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import java.util.List;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "permission")
@SQLDelete(sql = "UPDATE permission SET is_deleted = true WHERE permission_id = ?")
@SQLRestriction(value = "is_deleted = false")
public class Permission extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id")
    private Long id;
    @Column(name = "permission_name")
    private String name;
    @Column(name = "permission_desc")
    private String description;
    @ManyToMany(mappedBy = "permissions")
    private List<Role> roles;
}
