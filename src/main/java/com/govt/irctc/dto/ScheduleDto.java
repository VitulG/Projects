package com.govt.irctc.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class ScheduleDto {
    private String trainName;
    private Long trainNumber;
    private String scheduleTitle;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private String arrivalCity;
    private String destinationCity;
    private String duration;
    private double totalDistance;
    private List<String> scheduledDays;

}
