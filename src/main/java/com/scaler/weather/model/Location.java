package com.scaler.weather.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long locationId;

    private String locationName;
    private String region;
    private String country;
    private String localDateTime;

    @OneToMany(mappedBy = "location", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<CurrentWeather> weatherHistory;

}
