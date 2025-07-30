package com.example.book.repository;

import com.example.book.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findByUserName(String userName);

    User findByUserNameAndRefreshToken(String userName, String refreshToken);
}
