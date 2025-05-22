package com.seproject.demo.repository;

import com.seproject.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // JpaRepository 自带很多CRUD方法，不用写额外代码
    Optional<User> findByUsername(String username);
}