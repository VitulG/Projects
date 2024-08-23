package com.govt.irctc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeatDto {
    private Long trainNumber;
    private int totalNumberOfSeats;
    private int oneAcSeats;
    private int twoAcSeats;
    private int threeAcSeats;
    private int sleeperSeats;
    private int generalSeats;
    private double firstAcPrice;
    private double secondAcPrice;
    private double thirdAcPrice;
    private double sleeperPrice;
    private double generalPrice;
}
