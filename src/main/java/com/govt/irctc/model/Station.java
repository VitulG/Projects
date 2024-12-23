package com.govt.irctc.model;

import com.govt.irctc.enums.StationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Station extends BaseModel {
    private String stationName;
    private String city;
    private StationStatus stationStatus;

    @OneToMany(mappedBy = "station", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Platform> platforms;

    @ManyToMany
    @JoinTable( name = "station_routes",
            joinColumns = @JoinColumn(name = "station_id"),
            inverseJoinColumns = @JoinColumn(name = "route_id") )
    private List<Route> stationRoutes;

    @ManyToMany
    @JoinTable(
            name = "station_trains",
            joinColumns = @JoinColumn(name = "station_id"),
            inverseJoinColumns = @JoinColumn(name = "train_id")  // Assuming Train has a foreign key to Station with name "train_id"
    )
    private List<Train> trains;

}
