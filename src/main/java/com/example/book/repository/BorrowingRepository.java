package com.example.book.repository;

import com.example.book.constant.BorrowingType;
import com.example.book.entity.Book;
import com.example.book.entity.Borrowing;
import com.example.book.entity.Post;
import com.example.book.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowingRepository  extends JpaRepository<Borrowing, Long> {

    @Query("SELECT b FROM Borrowing b ORDER BY CASE WHEN b.returnDate IS NULL THEN 0 ELSE 1 END ASC, b.returnDate DESC")
    Page<Borrowing> findAllCustomSort(Pageable pageable);

    @Query("SELECT b.book FROM Borrowing b WHERE b.status = com.example.book.constant.BorrowingType.BORROWING GROUP BY b.book ORDER BY COUNT(b) DESC")
    List<Book> findCurrentBorrowingBooks();

    @Query("SELECT b FROM Borrowing b WHERE b.returnDate IS NULL")
    List<Borrowing> findByStatusBorrowingOrDue();

    List<Borrowing> findByStatus(BorrowingType borrowingType);
}
