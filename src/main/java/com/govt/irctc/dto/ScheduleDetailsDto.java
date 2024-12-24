package com.govt.irctc.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ScheduleDetailsDto {
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String arrivalCity;
    private String destinationCity;
    private Duration duration;
    private double totalDistance;
    private List<String> scheduledDays;
}
