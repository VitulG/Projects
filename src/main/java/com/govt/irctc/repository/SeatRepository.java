package com.govt.irctc.repository;

import com.govt.irctc.enums.SeatStatus;
import com.govt.irctc.model.Compartment;
import com.govt.irctc.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findAllByCompartmentAndSeatStatus(Compartment compartment, SeatStatus seatStatus);
}
