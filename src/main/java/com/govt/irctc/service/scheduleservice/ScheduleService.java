package com.govt.irctc.service.scheduleservice;

import com.govt.irctc.dto.ScheduleDetailsDto;
import com.govt.irctc.dto.ScheduleDto;
import com.govt.irctc.exceptions.ScheduleExceptions.ScheduleCreationException;
import com.govt.irctc.exceptions.SecurityExceptions.InvalidTokenException;
import com.govt.irctc.exceptions.SecurityExceptions.TokenNotFoundException;
import com.govt.irctc.exceptions.SecurityExceptions.UnauthorizedUserException;
import com.govt.irctc.exceptions.TrainExceptions.TrainNotFoundException;

import java.util.List;

public interface ScheduleService {
    public String setTrainSchedule(String token, Long trainNumber, ScheduleDetailsDto scheduleDetails) throws TokenNotFoundException, InvalidTokenException, UnauthorizedUserException, TrainNotFoundException, ScheduleCreationException;
    public List<ScheduleDto> getTrainSchedules(String token, Long trainNumber) throws InvalidTokenException, TokenNotFoundException, TrainNotFoundException;
}
