package com.govt.irctc.model;

import com.govt.irctc.enums.CompartmentType;
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
public class Compartment extends BaseModel {
    private String compartmentNumber;
    private int totalSeats;

    @Enumerated(EnumType.STRING)
    private CompartmentType compartmentType;

    @ManyToOne
    private Train train;

    @OneToMany(mappedBy = "compartment",cascade = CascadeType.ALL)
    private List<Seat> compartmentSeats;
}
