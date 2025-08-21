package com.example.book.repository;

import com.example.book.constant.RoleType;
import com.example.book.entity.Role;
import org.hibernate.sql.ast.tree.expression.JdbcParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
    Role findByRoleName(RoleType roleName);

    boolean existsByRoleName(RoleType roleName);
}
