package com.govt.irctc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private Long trainNumber;
    private String pnr;
    private LocalDateTime bookingDate;
    private List<SeatDto> seats;
    private String compartmentType;
    private int numberOfPassengers;
    private String ticketStatus;
    private double totalPrice;
    private String paymentStatus;
    private String errorMessage;
}
