package com.govt.irctc.service.seatservice;


import com.govt.irctc.dto.SeatDto;
import com.govt.irctc.dto.ShowSeatDto;
import com.govt.irctc.enums.UserRole;
import com.govt.irctc.exceptions.SeatExceptions.SeatsNotFoundException;
import com.govt.irctc.exceptions.SecurityExceptions.InvalidTokenException;
import com.govt.irctc.exceptions.SecurityExceptions.UnauthorizedUserException;
import com.govt.irctc.exceptions.TrainExceptions.TrainNotFoundException;
import com.govt.irctc.model.*;
import com.govt.irctc.repository.SeatRepository;
import com.govt.irctc.repository.TokenRepository;
import com.govt.irctc.repository.TrainRepository;
import com.govt.irctc.validation.TokenValidation;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SeatServiceImpl implements SeatService{
    private final TrainRepository trainRepository;
    private final SeatRepository seatRepository;
    private final TokenRepository tokenRepository;
    private final TokenValidation tokenValidation;

    @Autowired
    public SeatServiceImpl(TrainRepository trainRepository, SeatRepository seatRepository,
                           TokenRepository tokenRepository, TokenValidation tokenValidation) {
        this.trainRepository = trainRepository;
        this.seatRepository = seatRepository;
        this.tokenRepository = tokenRepository;
        this.tokenValidation = tokenValidation;
    }

    @Override
    @Transactional
    public String addSeats(SeatDto seatDto, String token) throws TrainNotFoundException,
            InvalidTokenException, UnauthorizedUserException {
        Optional<Token> optionalToken = tokenRepository.findByToken(token);

        if(optionalToken.isEmpty()){
            throw new InvalidTokenException("Token is invalid");
        }

        User existingUser = optionalToken.get().getUserTokens();

        boolean isAdmin = existingUser.getUserRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(UserRole.ADMIN.toString()));

        if(!isAdmin) {
            throw new UnauthorizedUserException("User is unauthorized to add the seats");
        }

        Optional<Train> optionalTrain = trainRepository.findByTrainNumber(seatDto.getTrainNumber());

        if(optionalTrain.isEmpty()) {
            throw new TrainNotFoundException("train is not available");
        }

        Seats seats = new Seats();
        seats.setGeneralSeats(seatDto.getGeneralSeats());
        seats.setSleeperSeats(seatDto.getSleeperSeats());
        seats.setOneAcSeats(seatDto.getOneAcSeats());
        seats.setThreeAcSeats(seatDto.getThreeAcSeats());
        seats.setTwoAcSeats(seatDto.getTwoAcSeats());
        seats.setTotalNumberOfSeats(seatDto.getTotalNumberOfSeats());
        seats.setGeneralPrice(seatDto.getGeneralPrice());
        seats.setSleeperPrice(seatDto.getSleeperPrice());
        seats.setThirdAcPrice(seatDto.getThirdAcPrice());
        seats.setSecondAcPrice(seatDto.getSecondAcPrice());
        seats.setFirstAcPrice(seatDto.getFirstAcPrice());
        Train train = optionalTrain.get();

        seats.setTrain(train);
        seatRepository.save(seats);

        if(train.getTrainSeats() == null) {
            train.setTrainSeats(new ArrayList<>());
        }

        train.getTrainSeats().add(seats);
        trainRepository.save(train);

        return "seats added successfully";
    }

    @Override
    public ShowSeatDto showSeats(Long trainNumber, String token) throws InvalidTokenException,
            TrainNotFoundException, SeatsNotFoundException {
        Optional<Token> optionalToken = tokenRepository.findByToken(token);

        if(optionalToken.isEmpty()) {
            throw new InvalidTokenException("token is invalid");
        }

        Optional<Train> optionalTrain = trainRepository.findByTrainNumber(trainNumber);

        if(optionalTrain.isEmpty()) {
            throw new TrainNotFoundException("train is not available");
        }

        Train train = optionalTrain.get();

        List<Seats> seat = train.getTrainSeats();

        if(seat.isEmpty()) {
            throw new SeatsNotFoundException("seats are not available for this train");
        }

        ShowSeatDto showSeatDto = new ShowSeatDto();
        showSeatDto.setTrainNumber(trainNumber);
        showSeatDto.setAvailableTotalNumberOfSeats(seat.get(0).getTotalNumberOfSeats());
        showSeatDto.setAvailableOneAcSeats(seat.get(0).getOneAcSeats());
        showSeatDto.setAvailableTwoAcSeats(seat.get(0).getTwoAcSeats());
        showSeatDto.setAvailableThreeAcSeats(seat.get(0).getThreeAcSeats());
        showSeatDto.setAvailableSleeperSeats(seat.get(0).getSleeperSeats());
        showSeatDto.setAvailableGeneralSeats(seat.get(0).getGeneralSeats());
        return showSeatDto;
    }

    @Override
    @Transactional
    public String updateSeatsByTrainNumber(Long trainNumber, SeatDto seatDto, String token) throws InvalidTokenException, TrainNotFoundException, SeatsNotFoundException, UnauthorizedUserException {
        Optional<Token> optionalToken = tokenRepository.findByToken(token);
        if(optionalToken.isEmpty()) {
            throw new InvalidTokenException("token is invalid");
        }
        Optional<Train> optionalTrain = trainRepository.findByTrainNumber(trainNumber);
        if(optionalTrain.isEmpty()) {
            throw new TrainNotFoundException("train is not available");
        }

        User existingUser = optionalToken.get().getUserTokens();

        boolean isAdmin = existingUser.getUserRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(UserRole.ADMIN.toString()));

        if(!isAdmin) {
            throw new UnauthorizedUserException("User is unauthorized to update the seats");
        }

        Train train = optionalTrain.get();

        List<Seats> seats = train.getTrainSeats();
        if(seats.isEmpty()) {
            throw new SeatsNotFoundException("seat is not available");
        }

        Seats seat = seats.get(0);
        seat.setTotalNumberOfSeats(seatDto.getTotalNumberOfSeats());
        seat.setOneAcSeats(seatDto.getOneAcSeats());
        seat.setThreeAcSeats(seatDto.getThreeAcSeats());
        seat.setTwoAcSeats(seatDto.getTwoAcSeats());
        seat.setSleeperSeats(seatDto.getSleeperSeats());
        seat.setGeneralSeats(seatDto.getGeneralSeats());
        seat.setFirstAcPrice(seatDto.getFirstAcPrice());
        seat.setSecondAcPrice(seatDto.getSecondAcPrice());
        seat.setThirdAcPrice(seatDto.getThirdAcPrice());
        seat.setSleeperPrice(seatDto.getSleeperPrice());
        seat.setGeneralPrice(seatDto.getGeneralPrice());
        seat.setUpdatedAt(LocalDateTime.now());
        seatRepository.save(seat);
        return "seats updated successfully for the train number:" + trainNumber;
    }

    @Override
    @Transactional
    public String deleteSeatsByTrainNumber(Long trainNumber, String token) throws InvalidTokenException, TrainNotFoundException, SeatsNotFoundException, UnauthorizedUserException {
        Optional<Token> optionalToken = tokenRepository.findByToken(token);

        if(optionalToken.isEmpty()) {
            throw new InvalidTokenException("token is invalid");
        }

        Token existingToken = optionalToken.get();

        if(!tokenValidation.isTokenValid(existingToken.getToken())) {
            throw new InvalidTokenException("either token is expired or deleted");
        }

        boolean isAdmin = existingToken.getUserTokens().getUserRoles()
                .stream().anyMatch(role -> role.getName().equalsIgnoreCase(UserRole.ADMIN.toString()));

        if(!isAdmin) {
            throw new UnauthorizedUserException("User is unauthorized to delete");
        }

        Optional<Train> optionalTrain = trainRepository.findByTrainNumber(trainNumber);
        if(optionalTrain.isEmpty()) {
            throw new TrainNotFoundException("train is not available");
        }

        Train train = optionalTrain.get();
        List<Seats> seats = train.getTrainSeats();
        if(seats.isEmpty()) {
            throw new SeatsNotFoundException("seats are not available for this train");
        }

        Seats seat = seats.get(0);
        train.getTrainSeats().remove(seat);
        seat.setDeleted(true);
        seatRepository.save(seat);
        return "seats deleted successfully for the train number:" + trainNumber;
    }

}
