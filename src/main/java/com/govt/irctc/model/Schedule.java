package com.govt.irctc.model;

import com.govt.irctc.dto.ScheduleDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Schedule extends BaseModel {
    private String scheduleTitle;
    private LocalTime departureTime;
    private LocalTime arrivalTime;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<DayOfWeek> dayOfWeek;

    private String arrivalCity;
    private String destinationCity;
    private Duration duration;
    private double totalDistance;

    @ManyToOne
    private Train train;

    public ScheduleDto buildScheduleDto() {
        ScheduleDto scheduleDto = new ScheduleDto();
        scheduleDto.setScheduleTitle(getScheduleTitle());
        scheduleDto.setTrainNumber(train.getTrainNumber());
        scheduleDto.setTrainName(train.getTrainName());
        scheduleDto.setTotalDistance(getTotalDistance());
        scheduleDto.setDestinationCity(getDestinationCity());
        scheduleDto.setArrivalCity(getArrivalCity());
        scheduleDto.setArrivalTime(getArrivalTime());
        scheduleDto.setDepartureTime(getDepartureTime());
        scheduleDto.setDuration(convertToTimeFormat(getDuration()));
        scheduleDto.setScheduledDays(getDayOfWeek()
                .stream()
                .map(String::valueOf)
                .toList());
        return scheduleDto;
    }

    private String convertToTimeFormat(Duration duration) {
        return duration.toHoursPart() + " hours " + duration.toMinutesPart() + " minutes";
    }
}
