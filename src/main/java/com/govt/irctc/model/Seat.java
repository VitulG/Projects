package com.govt.irctc.model;

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

    @ManyToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private SeatStatus seatStatus;

    public SeatDto convertToSeatDto() {
        SeatDto seatDto = new SeatDto();
        seatDto.setSeatType(compartment.getCompartmentType().toString());
        List<String> seats = new ArrayList<>();
        for(Seat seat : user.getUserSeats()) {
            seats.add(seat.getSeatNumber());
        }
        seatDto.setSeatNumber(seats);
        return seatDto;
    }

}
