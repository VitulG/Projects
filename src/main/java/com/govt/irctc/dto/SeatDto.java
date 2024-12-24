package com.govt.irctc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeatDto {
    private Long trainNumber;
    private String compartment;
    private String seatNumber;
    private String seatType;
    private String isWindowSeat;
    private String seatStatus;
}
