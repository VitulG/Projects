package com.govt.irctc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDetailsDto {
    private String name;
    private String email;
    private Long trainNumber;
    private String from;
    private String to;
    private String compartmentType;
    private int numberOfPassengers;
    private List<String> paymentMethods;
}
