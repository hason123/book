package com.example.book.repository;

import com.example.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> , JpaSpecificationExecutor<Book> {
/*
    @Query("SELECT b FROM Book b WHERE " +
            "LOWER(b.bookId) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(b.bookDesc) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(b.bookName) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    List<BookResponseDTO> findBooksBySearchText(@Param("searchText") String keyword);
*/
    boolean existsByBookName(String title);

}
