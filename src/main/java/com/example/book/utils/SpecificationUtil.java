package com.example.book.utils;

import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class SpecificationUtil {
    public static <T> Specification<T> uploadBeforeDate(LocalDate uploadDate) {
        LocalDateTime endOfDay = uploadDate.plusDays(1).atStartOfDay();
        return (root, query, cb) -> cb.lessThan(root.get("createdTime"), endOfDay);
    }

    public static <T> Specification<T> uploadAfterDate(LocalDate uploadDate) {
        LocalDateTime startOfDay = uploadDate.atStartOfDay();
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdTime"), startOfDay);
    }
}
