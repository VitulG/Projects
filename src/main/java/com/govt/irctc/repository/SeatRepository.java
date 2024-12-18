package com.govt.irctc.repository;

import com.govt.irctc.model.Seats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seats, Long> {
}
