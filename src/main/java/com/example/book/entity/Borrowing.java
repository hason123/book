package com.example.book.entity;

import com.example.book.constant.BorrowingType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Borrowing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate borrowDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate returnDate;
    @ManyToOne
    @JoinColumn(name = "borrowing_user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "borrowing_book_id")
    private Book book;
    @Enumerated(EnumType.STRING)
    private BorrowingType status;


}
