package com.govt.irctc.service.seatservice;


import com.govt.irctc.dto.SeatDto;
import com.govt.irctc.dto.ShowSeatDto;
import com.govt.irctc.exceptions.SeatExceptions.SeatsNotFoundException;
import com.govt.irctc.exceptions.SecurityExceptions.InvalidTokenException;
import com.govt.irctc.exceptions.SecurityExceptions.UnauthorizedUserException;
import com.govt.irctc.exceptions.TrainExceptions.TrainNotFoundException;
import com.govt.irctc.model.*;
import com.govt.irctc.repository.SeatRepository;
import com.govt.irctc.repository.TokenRepository;
import com.govt.irctc.repository.TrainRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SeatServiceImpl implements SeatService{
    private final TrainRepository trainRepository;
    private final SeatRepository seatRepository;
    private final TokenRepository tokenRepository;

    @Autowired
    public SeatServiceImpl(TrainRepository trainRepository, SeatRepository seatRepository,
                           TokenRepository tokenRepository) {
        this.trainRepository = trainRepository;
        this.seatRepository = seatRepository;
        this.tokenRepository = tokenRepository;
    }

    @Override
    @Transactional
    public String addSeats(SeatDto seatDto, String token) throws TrainNotFoundException,
            InvalidTokenException, UnauthorizedUserException {
        Optional<Token> optionalToken = tokenRepository.findByTokenValue(token);



        return "seats added successfully";
    }

    @Override
    public ShowSeatDto showSeats(Long trainNumber, String token) throws InvalidTokenException,
            TrainNotFoundException, SeatsNotFoundException {
        Optional<Token> optionalToken = tokenRepository.findByTokenValue(token);

        if(optionalToken.isEmpty()) {
            throw new InvalidTokenException("token is invalid");
        }

        Optional<Train> optionalTrain = trainRepository.findByTrainNumber(trainNumber);

        if(optionalTrain.isEmpty()) {
            throw new TrainNotFoundException("train is not available");
        }

        Train train = optionalTrain.get();


        return null;
    }

    @Override
    @Transactional
    public String updateSeatsByTrainNumber(Long trainNumber, SeatDto seatDto, String token) throws InvalidTokenException, TrainNotFoundException, SeatsNotFoundException, UnauthorizedUserException {
        Optional<Token> optionalToken = tokenRepository.findByTokenValue(token);
        if(optionalToken.isEmpty()) {
            throw new InvalidTokenException("token is invalid");
        }
        Optional<Train> optionalTrain = trainRepository.findByTrainNumber(trainNumber);
        if(optionalTrain.isEmpty()) {
            throw new TrainNotFoundException("train is not available");
        }

        User existingUser = optionalToken.get().getUserTokens();

//        boolean isAdmin = existingUser.getUserRoles().stream()
//                .anyMatch(role -> role.getName().equalsIgnoreCase(UserRole.ADMIN.toString()));
//
//        if(!isAdmin) {
//            throw new UnauthorizedUserException("User is unauthorized to update the seats");
//        }

        Train train = optionalTrain.get();

        return "seats updated successfully for the train number:" + trainNumber;
    }

    @Override
    @Transactional
    public String deleteSeatsByTrainNumber(Long trainNumber, String token) throws InvalidTokenException, TrainNotFoundException, SeatsNotFoundException, UnauthorizedUserException {
        Optional<Token> optionalToken = tokenRepository.findByTokenValue(token);

        if(optionalToken.isEmpty()) {
            throw new InvalidTokenException("token is invalid");
        }

        Token existingToken = optionalToken.get();

//        if(!tokenValidation.isTokenValid(existingToken.getTokenValue())) {
//            throw new InvalidTokenException("either token is expired or deleted");
//        }

//        boolean isAdmin = existingToken.getUserTokens().getUserRoles()
//                .stream().anyMatch(role -> role.getName().equalsIgnoreCase(UserRole.ADMIN.toString()));
//
//        if(!isAdmin) {
//            throw new UnauthorizedUserException("User is unauthorized to delete");
//        }

        Optional<Train> optionalTrain = trainRepository.findByTrainNumber(trainNumber);
        if(optionalTrain.isEmpty()) {
            throw new TrainNotFoundException("train is not available");
        }

        Train train = optionalTrain.get();

        return "seats deleted successfully for the train number:" + trainNumber;
    }

}
