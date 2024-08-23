package com.govt.irctc.model;

import com.govt.irctc.dto.SeatDto;
import com.govt.irctc.dto.TrainDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Train extends BaseModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String trainName;
    private TrainType trainType;
    private Long trainNumber;
    private String trainArrivalCity;
    private String trainDestinationCity;
    private LocalTime startTime;
    private LocalTime endTime;
    private int platformNumber;

    @OneToMany(mappedBy = "trains", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    List<Booking> bookings;

    @OneToMany(mappedBy = "train", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    List<Seats> trainSeats;

    public TrainDto convertToDto() {
        TrainDto trainDto = new TrainDto();
        trainDto.setTrainName(trainName);
        trainDto.setTrainType(trainType.toString());
        trainDto.setTrainNumber(trainNumber);
        trainDto.setTrainArrivalCity(trainArrivalCity);
        trainDto.setTrainDestinationCity(trainDestinationCity);
        trainDto.setStartTime(startTime);
        trainDto.setEndTime(endTime);
        trainDto.setPlatformNumber(platformNumber);

        List<SeatDto> seatDto = new ArrayList<>();

        for(Seats seat : trainSeats) {
            seatDto.add(seat.convertToDto());
        }

        trainDto.setSeats(seatDto);

        return trainDto;
    }
}
