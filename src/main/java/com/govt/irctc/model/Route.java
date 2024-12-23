package com.govt.irctc.model;

import com.govt.irctc.enums.RouteStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Route extends BaseModel {
    private String routeNumber;

    @ManyToMany(mappedBy = "routes")
    private List<Train> train;

    @ManyToMany
    @JoinTable( name = "route_stations",
            joinColumns = @JoinColumn(name = "route_id"),
            inverseJoinColumns = @JoinColumn(name = "station_id") )
    private List<Station> stations;

    @Enumerated(EnumType.STRING)
    private RouteStatus routeStatus;
}
