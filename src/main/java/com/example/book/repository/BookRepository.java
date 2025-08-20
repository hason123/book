package com.example.book.repository;

import com.example.book.dto.ResponseDTO.BookResponseDTO;
import com.example.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> , JpaSpecificationExecutor<Book> {
/*
    @Query("SELECT b FROM Book b WHERE " +
            "LOWER(b.bookId) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(b.bookDesc) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(b.bookName) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    List<BookResponseDTO> findBooksBySearchText(@Param("searchText") String keyword);
*/


}
