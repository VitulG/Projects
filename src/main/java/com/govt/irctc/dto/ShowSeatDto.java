package com.govt.irctc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShowSeatDto {
    private Long trainNumber;
    private int availableTotalNumberOfSeats;
    private int availableOneAcSeats;
    private int availableTwoAcSeats;
    private int availableThreeAcSeats;
    private int availableSleeperSeats;
    private int availableGeneralSeats;
}
