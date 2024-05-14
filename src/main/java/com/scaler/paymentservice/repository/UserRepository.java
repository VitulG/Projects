package com.scaler.paymentservice.repository;


import com.scaler.paymentservice.entity.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @NotNull
    @Override
    User save(@NotNull User user);
}
