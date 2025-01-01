package com.govt.irctc.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookedSeatDto {
    private String seatNumber;
    private String seatType;
    private String isWindowSeat;
    private String seatStatus;
}
