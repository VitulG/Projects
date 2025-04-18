package com.rghousing.realestate.repositories;

import com.rghousing.realestate.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByUserEmail(String email);

    boolean existsUserByUserEmail(String email);
}
