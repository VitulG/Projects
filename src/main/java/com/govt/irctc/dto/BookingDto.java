package com.govt.irctc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private String name;
    private String email;
    private Long pnr;
    private LocalDateTime bookingDate;
    private String seatType;
    private Long trainNumber;
    private int numberOfPassengers;
    private String ticketStatus;
    private double totalPrice;
    private String paymentStatus;
}
