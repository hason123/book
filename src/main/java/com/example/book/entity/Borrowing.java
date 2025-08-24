package com.example.book.entity;

import com.example.book.constant.BorrowingType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "borrowing")
public class Borrowing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "borrowing_id")
    private Long id;
    @Column(name = "borrow_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate borrowDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "return_date")
    private LocalDate returnDate;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private BorrowingType status;
    @ManyToOne
    @JoinColumn(name = "borrowing_user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "borrowing_book_id")
    private Book book;

}
