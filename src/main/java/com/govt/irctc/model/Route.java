package com.govt.irctc.model;

import com.govt.irctc.enums.RouteStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Route extends BaseModel {
    @ManyToOne
    private Train train;

    private String stationName;

    private LocalTime arrivalTime;
    private LocalTime departureTime;

    private int platformNumber;

    @Enumerated(EnumType.STRING)
    private RouteStatus trainStatus;
}
