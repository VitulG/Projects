package com.govt.irctc.service.trainservice;

import com.govt.irctc.advice.LoginAdvice.InvalidTokenException;
import com.govt.irctc.advice.TrainAdvice.TrainCreationException;
import com.govt.irctc.advice.TrainAdvice.TrainNotFoundException;
import com.govt.irctc.advice.UnauthorizedUserException;
import com.govt.irctc.dto.TrainDto;

import java.util.List;

public interface TrainService {
    public String addTrain(TrainDto trainDto, String token) throws TrainCreationException,
            InvalidTokenException, UnauthorizedUserException;
    public TrainDto getTrainById(Long id, String token) throws TrainNotFoundException, InvalidTokenException;
    public List<TrainDto> getAllTrains(String token) throws InvalidTokenException, TrainNotFoundException;
    public String updateTrainById(Long trainNumber, TrainDto trainDto, String token) throws TrainNotFoundException,
            InvalidTokenException, UnauthorizedUserException;
    public String deleteTrainByTrainNumber(Long trainNumber, String token) throws TrainNotFoundException,
            InvalidTokenException, UnauthorizedUserException;
}
