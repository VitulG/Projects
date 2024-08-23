package com.govt.irctc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDetailsDto {
    private String name;
    private String email;
    private String seatType;
    private Long trainNumber;
    private int numberOfPassengers;
    private String token;
}
