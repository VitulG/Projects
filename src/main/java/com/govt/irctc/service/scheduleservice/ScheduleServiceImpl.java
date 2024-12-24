package com.govt.irctc.service.scheduleservice;

import com.govt.irctc.dto.ScheduleDetailsDto;
import com.govt.irctc.dto.ScheduleDto;
import com.govt.irctc.enums.UserRole;
import com.govt.irctc.exceptions.ScheduleExceptions.ScheduleCreationException;
import com.govt.irctc.exceptions.SecurityExceptions.InvalidTokenException;
import com.govt.irctc.exceptions.SecurityExceptions.TokenNotFoundException;
import com.govt.irctc.exceptions.SecurityExceptions.UnauthorizedUserException;
import com.govt.irctc.exceptions.TrainExceptions.TrainNotFoundException;
import com.govt.irctc.model.Schedule;
import com.govt.irctc.model.Token;
import com.govt.irctc.model.Train;
import com.govt.irctc.model.User;
import com.govt.irctc.repository.ScheduleRepository;
import com.govt.irctc.repository.TokenRepository;
import com.govt.irctc.repository.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    private final TrainRepository trainRepository;
    private final ScheduleRepository scheduleRepository;
    private final TokenRepository tokenRepository;

    @Autowired
    public ScheduleServiceImpl(TokenRepository tokenRepository, TrainRepository trainRepository, ScheduleRepository scheduleRepository) {
        this.tokenRepository = tokenRepository;
        this.trainRepository = trainRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public String setTrainSchedule(String token, Long trainNumber, ScheduleDetailsDto scheduleDetails) throws TokenNotFoundException,
            InvalidTokenException, UnauthorizedUserException, TrainNotFoundException, ScheduleCreationException {
        Token existingToken = getAndValidateToken(token);

        User existingUser = existingToken.getUserTokens();

        if(existingUser.getUserRole() != UserRole.ADMIN) {
            throw new UnauthorizedUserException("User is not authorized to set the schedule");
        }

        Train train = trainRepository.findByTrainNumber(trainNumber)
                .orElseThrow(() -> new TrainNotFoundException("Train Not Found"));

        if(train.getSchedules() == null) {
            train.setSchedules(new ArrayList<>());
        }

        Optional<Schedule> optionalSchedule = scheduleRepository.findByScheduleTitle(scheduleDetails.getScheduleTitle());

        if(optionalSchedule.isPresent()) {
            throw new ScheduleCreationException("Schedule already exists");
        }

        // validation logic for schedule here

        Schedule schedule = new Schedule();
        schedule.setScheduleTitle(scheduleDetails.getScheduleTitle());
        schedule.setTotalDistance(scheduleDetails.getTotalDistance());
        schedule.setArrivalCity(scheduleDetails.getArrivalCity());
        schedule.setDestinationCity(scheduleDetails.getDestinationCity());
        schedule.setDuration(scheduleDetails.getDuration());
        schedule.setDepartureTime(scheduleDetails.getDepartureTime());
        schedule.setArrivalTime(scheduleDetails.getArrivalTime());

        if(schedule.getDayOfWeek() == null) {
            schedule.setDayOfWeek(new HashSet<>());
        }

        schedule.setTrain(train);

        schedule.setDayOfWeek(scheduleDetails.getScheduledDays()
                .stream()
                .map(String::toUpperCase)
                .map(DayOfWeek::valueOf)
                .collect(Collectors.toSet()));

        if(train.getSchedules() == null) {
            train.setSchedules(new ArrayList<>());
        }
        train.getSchedules().add(schedule);
        scheduleRepository.save(schedule);
        trainRepository.save(train);
        return "Schedule created for the train number: "+schedule.getTrain().getTrainNumber();
    }

    @Override
    public List<ScheduleDto> getTrainSchedules(String token, Long trainNumber) throws InvalidTokenException, TokenNotFoundException, TrainNotFoundException {
        Token existingToken = getAndValidateToken(token);

        Train train = trainRepository.findByTrainNumber(trainNumber)
                .orElseThrow(() -> new TrainNotFoundException("Train not found"));

        List<Schedule> trainSchedules = scheduleRepository.findAllByTrain(train);

        List<ScheduleDto> scheduleDtos = new ArrayList<>();

        for(Schedule schedule : trainSchedules) {
            scheduleDtos.add(schedule.buildScheduleDto());
        }
        return scheduleDtos;
    }

    private Token getAndValidateToken(String token) throws TokenNotFoundException, InvalidTokenException {
        // implement token validation logic here
        Token existingToken = tokenRepository.findByTokenValue(token)
                .orElseThrow(() -> new TokenNotFoundException("Token not found"));

        if(existingToken.isDeleted() || existingToken.getTokenValidity().before(new Date())) {
            throw new InvalidTokenException("Either token is deleted or expired");
        }
        return existingToken;
    }
}
