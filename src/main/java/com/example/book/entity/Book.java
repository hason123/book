package com.example.book.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;
    private String bookName;
    private String author;
    private String publisher;
    private Integer pageCount;
    private String printType;
    private String language;
    private Integer quantity;
    private String bookDesc;

    @ManyToMany
    @JoinTable(
            name = "book_category",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;

    @OneToMany(mappedBy = "book")
    private List<Borrowing> borrowing;

}
