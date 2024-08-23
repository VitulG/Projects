package com.govt.irctc.repository;

import com.govt.irctc.model.Booking;
import com.govt.irctc.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserEmail(String userEmail);
    //List<User> findAll();

    @Modifying
    @Transactional
    @Query("DELETE FROM User u WHERE u.userEmail = :email")
    void deleteUserByUserEmail(@Param("email") String email);
}
