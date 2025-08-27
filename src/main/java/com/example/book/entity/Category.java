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
@Table(name="category")
@SQLDelete(sql = "UPDATE category SET is_deleted = true WHERE category_id = ?")
@SQLRestriction(value = "is_deleted = false")
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;
    @Column(name = "category_name")
    private String categoryName;
    @ManyToMany (mappedBy = "categories")
    private List<Book> books;

}
