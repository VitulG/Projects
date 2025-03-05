package com.govt.irctc.service.trainservice;


import com.govt.irctc.dto.TrainDto;
import com.govt.irctc.exceptions.SecurityExceptions.InvalidTokenException;
import com.govt.irctc.exceptions.SecurityExceptions.UnauthorizedUserException;
import com.govt.irctc.exceptions.TrainExceptions.TrainCreationException;
import com.govt.irctc.exceptions.TrainExceptions.TrainNotFoundException;

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
    List<TrainDto> searchTrainsByName(String trainName) throws TrainNotFoundException;
}
