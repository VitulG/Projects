package com.govt.irctc.service.trainservice;


import com.govt.irctc.dto.TrainDto;
import com.govt.irctc.enums.TrainType;
import com.govt.irctc.exceptions.SecurityExceptions.InvalidTokenException;
import com.govt.irctc.exceptions.SecurityExceptions.UnauthorizedUserException;
import com.govt.irctc.exceptions.TrainExceptions.TrainCreationException;
import com.govt.irctc.exceptions.TrainExceptions.TrainNotFoundException;
import com.govt.irctc.model.*;
import com.govt.irctc.repository.TokenRepository;
import com.govt.irctc.repository.TrainRepository;
import com.govt.irctc.validation.TrainDetailsValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TrainServiceImpl implements TrainService{

    private final TrainRepository trainRepository;
    private final TokenRepository tokenRepository;
    private final TrainDetailsValidator trainDetailsValidator;

    @Autowired
    public TrainServiceImpl(TrainRepository trainRepository,
                            TokenRepository tokenRepository, TrainDetailsValidator trainDetailsValidator) {
        this.trainRepository = trainRepository;
        this.tokenRepository = tokenRepository;
        this.trainDetailsValidator = trainDetailsValidator;
    }

    @Override
    public String addTrain(TrainDto trainDto, String token) throws TrainCreationException, InvalidTokenException, UnauthorizedUserException {

        Optional<Token> optionalToken = tokenRepository.findByTokenValue(token);

        if(optionalToken.isEmpty()) {
            throw new InvalidTokenException("token is invalid");
        }

        User user = optionalToken.get().getUserTokens();

        boolean isAdmin = false;

        if (!isAdmin) {
            throw new UnauthorizedUserException("User is unauthorized");
        }

        Optional<Train> checkTrain = trainRepository.findByTrainNumber(trainDto.getTrainNumber());

        if(checkTrain.isPresent()) {
            throw new TrainCreationException("train already exists");
        }

        Train train = new Train();
//        train.setTrainDestinationCity(trainDto.getTrainDestinationCity());
//        train.setTrainArrivalCity(trainDto.getTrainArrivalCity());

//        if(!trainDetailsValidation.validateTrainNumber(trainDto.getTrainNumber())) {
//            throw new TrainCreationException("Invalid train number");
//        }
//
//        if(!trainDetailsValidation.validateTrainType(trainDto.getTrainType())) {
//            throw new TrainCreationException("Invalid train type");
//        }

        train.setTrainNumber(trainDto.getTrainNumber());
        train.setTrainName(trainDto.getTrainName());
        train.setTrainType(TrainType.valueOf(trainDto.getTrainType().toUpperCase()));
        train.setStartTime(trainDto.getStartTime());
        train.setEndTime(trainDto.getEndTime());
        train.setCreatedAt(LocalDateTime.now());
        train.setPlatformNumber(trainDto.getPlatformNumber());

        trainRepository.save(train);

        return "Train added successfully. Now please added the seats";
    }

    @Override
    public TrainDto getTrainById(Long trainNumber, String token) throws TrainNotFoundException, InvalidTokenException {

//        if(!tokenValidation.isTokenValid(token)) {
//            throw new InvalidTokenException("token is either expired or invalid");
//        }

        Optional<Train> optionalTrain = trainRepository.findByTrainNumber(trainNumber);
        if(optionalTrain.isEmpty()) {
            throw new TrainNotFoundException("train not exists");
        }
        if(optionalTrain.get().isDeleted()) {
            throw new TrainNotFoundException("Train doesn't exists");
        }
        return null;
    }

    @Override
    public List<TrainDto> getAllTrains(String token) throws InvalidTokenException, TrainNotFoundException {
//        if(!tokenValidation.isTokenValid(token)) {
//            throw new InvalidTokenException("Token is invalid");
//        }
        List<Train> trains = trainRepository.findAll();

        if(trains.isEmpty()) {
            throw new TrainNotFoundException("trains are not available");
        }

        List<TrainDto> trainsDto = new ArrayList<>();

        for(Train train : trains) {
            if(!train.isDeleted()) {

            }
        }
        return trainsDto;
    }

    @Override
    public String updateTrainById(Long trainNumber, TrainDto trainDto, String token) throws TrainNotFoundException,
            InvalidTokenException, UnauthorizedUserException {

        Optional<Token> optionalToken = tokenRepository.findByTokenValue(token);

        if(optionalToken.isEmpty()) {
            throw new InvalidTokenException("token is invalid");
        }

        User existingUser = optionalToken.get().getUserTokens();
//        boolean isAdmin = existingUser.getUserRoles().stream()
//                .anyMatch(role -> role.getName().equalsIgnoreCase(UserRole.ADMIN.toString()));
//
//        if (!isAdmin) {
//            throw new UnauthorizedUserException("User is not authorized to update");
//        }

        Optional<Train> optionalTrain = trainRepository.findByTrainNumber(trainNumber);

        if(optionalTrain.isEmpty()) {
            throw new TrainNotFoundException("train not exists");
        }

        Train train = optionalTrain.get();

//        train.setTrainDestinationCity(trainDto.getTrainDestinationCity());
//        train.setTrainNumber(trainDto.getTrainNumber());
//        train.setTrainArrivalCity(trainDto.getTrainArrivalCity());
        train.setTrainName(trainDto.getTrainName());
        train.setPlatformNumber(trainDto.getPlatformNumber());
        train.setTrainType(TrainType.valueOf(trainDto.getTrainType().toUpperCase()));
        train.setStartTime(trainDto.getStartTime());
        train.setEndTime(trainDto.getEndTime());
        train.setUpdatedAt(LocalDateTime.now());

        trainRepository.save(train);

        return "train details updated successfully";
    }

    @Override
    public String deleteTrainByTrainNumber(Long trainNumber, String token) throws TrainNotFoundException, InvalidTokenException, UnauthorizedUserException {
        Optional<Token> optionalToken = tokenRepository.findByTokenValue(token);

//        if(optionalToken.isEmpty() || !tokenValidation.isTokenValid(token)) {
//            throw new InvalidTokenException("token is invalid");
//        }

        User existingUser = optionalToken.get().getUserTokens();
//        boolean isAdmin = existingUser.getUserRoles().stream()
//                .anyMatch(role -> role.getName().equalsIgnoreCase(UserRole.ADMIN.toString()));
//
//        if (!isAdmin) {
//            throw new UnauthorizedUserException("User is not authorized to delete trains");
//        }

        Optional<Train> optionalTrain = trainRepository.findByTrainNumber(trainNumber);
        if(optionalTrain.isEmpty()) {
            throw new TrainNotFoundException("train not exists");
        }
        Train train = optionalTrain.get();
        train.setDeleted(true);
        trainRepository.save(train);
        return "train deleted successfully";
    }
}
