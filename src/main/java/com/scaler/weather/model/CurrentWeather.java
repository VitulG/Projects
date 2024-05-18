package com.scaler.weather.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CurrentWeather {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double current_temperature_in_C;
    private Double current_temperature_in_F;
    private Double wind_speed_in_miles;
    private Double wind_speed_in_kph;
    private int humidity;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Location location;

    @OneToOne(cascade = CascadeType.ALL)
    private CurrentCondition condition;

    @OneToOne(cascade = CascadeType.ALL)
    private AirQuality airQuality;

}
