package com.govt.irctc.model;

import com.govt.irctc.enums.TrainStatus;
import com.govt.irctc.enums.TrainType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Train extends BaseModel{
    private String trainName;

    @Enumerated(EnumType.STRING)
    private TrainType trainType;

    private Long trainNumber;

    private String arrivalCity;
    private String destinationCity;

    private LocalTime startTime;
    private LocalTime endTime;

    private int platformNumber;
    private int trainDuration;
    private double totalDistance;

    @Enumerated(EnumType.STRING)
    private TrainStatus trainStatus;

    @OneToMany(mappedBy = "trains", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings;

    @OneToMany(mappedBy = "train", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Compartment> trainCompartments;

    @OneToMany(mappedBy = "train", cascade = CascadeType.ALL)
    private List<Route> trainRoutes;

}
