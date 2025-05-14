package com.example.postproject.repository;

import com.example.postproject.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**interface of UserRepository.*/
public interface UserRepository extends JpaRepository<User, Long> {
    /**Find user by username.*/
    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);
}