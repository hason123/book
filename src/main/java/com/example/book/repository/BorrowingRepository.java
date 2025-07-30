package com.example.book.repository;

import com.example.book.entity.Borrowing;
import com.example.book.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BorrowingRepository  extends JpaRepository<Borrowing, Long> {

    @Query("SELECT b FROM Borrowing b ORDER BY CASE WHEN b.returnDate IS NULL THEN 0 ELSE 1 END ASC, b.returnDate DESC")
    Page<Borrowing> findAllCustomSort(Pageable pageable);
}
