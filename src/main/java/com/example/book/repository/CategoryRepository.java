package com.example.book.repository;

import com.example.book.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c, COUNT(b) FROM Category c LEFT JOIN c.books b GROUP BY c")
    List<Object[]> findCategoryAndBookCount();

}
