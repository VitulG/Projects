package com.govt.irctc.model;

import com.govt.irctc.dto.BookedSeatDto;
import com.govt.irctc.dto.SeatDto;
import com.govt.irctc.enums.SeatStatus;
import com.govt.irctc.enums.SeatType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Seat extends BaseModel {
    private String seatNumber;

    @Enumerated(EnumType.STRING)
    private SeatType seatType;

    @ManyToOne
    private Compartment compartment;

    private boolean isWindowSeat;

    @Enumerated(EnumType.STRING)
    private SeatStatus seatStatus;

    @ManyToOne
    private Booking booking;

    public BookedSeatDto convertToSeatDto() {
        BookedSeatDto seatDto =  new BookedSeatDto();
        seatDto.setSeatNumber(seatNumber);
        seatDto.setSeatType(getSeatType().toString().toLowerCase());
        seatDto.setIsWindowSeat(String.valueOf(isWindowSeat));
        seatDto.setSeatStatus(getSeatStatus().toString().toLowerCase());

        return seatDto;
    }
}
