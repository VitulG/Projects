package com.govt.irctc.model;

import com.govt.irctc.enums.TrainStatus;
import com.govt.irctc.enums.TrainType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "trains")
public class Train extends BaseModel{
    private String trainName;

    @Enumerated(EnumType.STRING)
    private TrainType trainType;

    private Long trainNumber;

    @OneToMany(mappedBy = "train", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Schedule> schedules;

    @Enumerated(EnumType.STRING)
    private TrainStatus trainStatus;

    @OneToMany(mappedBy = "trains", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings;

    @OneToMany(mappedBy = "train", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Compartment> compartments;

    @ManyToMany
    @JoinTable(
            name = "train_routes",
            joinColumns = @JoinColumn(name = "train_id"),
            inverseJoinColumns = @JoinColumn(name = "route_id")  // assuming route_id is the primary key of Route table. Replace with actual column name if different.
    )
    private List<Route> routes;

    @ManyToMany(mappedBy = "trains")
    private List<Station> stations;

    @ManyToOne
    private Platform platform;
}
