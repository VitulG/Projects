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
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByUserBookings(User user);

    @Modifying
    @Transactional
    @Query("UPDATE Booking b SET b.isDeleted = true WHERE b.userBookings.id =: userId")
    void deleteBookingByUserId(@Param("userId") Long userId);
}
