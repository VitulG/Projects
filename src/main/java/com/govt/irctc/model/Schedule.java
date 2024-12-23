package com.govt.irctc.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Schedule extends BaseModel {
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;

    @Enumerated(EnumType.STRING)
    @ElementCollection
    private List<DayOfWeek> dayOfWeek;

    private String arrivalCity;
    private String destinationCity;
    private Duration duration;
    private double totalDistance;

    @ManyToOne
    private Train train;
}
