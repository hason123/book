package com.example.book.repository;

import com.example.book.constant.RoleType;
import com.example.book.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
    Role findByRoleName(RoleType roleName);

    boolean existsByRoleName(RoleType roleName);
}
