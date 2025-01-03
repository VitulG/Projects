package com.govt.irctc.repository;

import com.govt.irctc.model.Address;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    Optional<Address> findByHouseNumber(int houseNumber);

    @Modifying
    @Transactional
    @Query("UPDATE Address a SET a.isDeleted = true WHERE a.user.id =:userId")
    public void deleteUserAddresses(@Param("userId") Long userId);
}
