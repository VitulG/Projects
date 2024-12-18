package com.govt.irctc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainDto {
    private String trainName;
    private String trainType;
    private Long trainNumber;
    private String trainArrivalCity;
    private String trainDestinationCity;
    private LocalTime startTime;
    private LocalTime endTime;
    private int platformNumber;
    private List<SeatDto> seats;
}
