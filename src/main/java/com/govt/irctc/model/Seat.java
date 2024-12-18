package com.govt.irctc.model;

import com.govt.irctc.enums.SeatStatus;
import com.govt.irctc.enums.SeatType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

}
