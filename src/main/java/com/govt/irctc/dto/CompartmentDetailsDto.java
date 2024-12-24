package com.govt.irctc.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompartmentDetailsDto {
    private String compartmentNumber;
    private int numberOfSeats;
    private double pricePerSeat;
    private String compartmentType;
    private Long trainNumber;

}
