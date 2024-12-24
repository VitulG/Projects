package com.govt.irctc.service.seatservice;


import com.govt.irctc.dto.SeatDto;
import com.govt.irctc.dto.ShowSeatDto;
import com.govt.irctc.enums.SeatStatus;
import com.govt.irctc.enums.SeatType;
import com.govt.irctc.enums.UserRole;
import com.govt.irctc.exceptions.CompartmentException.CompartmentNotFoundException;
import com.govt.irctc.exceptions.SeatExceptions.SeatNotCreatedException;
import com.govt.irctc.exceptions.SeatExceptions.SeatsNotFoundException;
import com.govt.irctc.exceptions.SecurityExceptions.InvalidTokenException;
import com.govt.irctc.exceptions.SecurityExceptions.TokenNotFoundException;
import com.govt.irctc.exceptions.SecurityExceptions.UnauthorizedUserException;
import com.govt.irctc.exceptions.TrainExceptions.TrainNotFoundException;
import com.govt.irctc.model.*;
import com.govt.irctc.repository.CompartmentRepository;
import com.govt.irctc.repository.SeatRepository;
import com.govt.irctc.repository.TokenRepository;
import com.govt.irctc.repository.TrainRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

@Service
public class SeatServiceImpl implements SeatService{
    private final TrainRepository trainRepository;
    private final SeatRepository seatRepository;
    private final TokenRepository tokenRepository;
    private final CompartmentRepository compartmentRepository;

    @Autowired
    public SeatServiceImpl(TrainRepository trainRepository, SeatRepository seatRepository,
                           TokenRepository tokenRepository, CompartmentRepository compartmentRepository) {
        this.trainRepository = trainRepository;
        this.seatRepository = seatRepository;
        this.tokenRepository = tokenRepository;
        this.compartmentRepository = compartmentRepository;
    }

    @Override
    public String addSeats(SeatDto seatDto, String token) throws TrainNotFoundException,
            InvalidTokenException, UnauthorizedUserException, TokenNotFoundException, CompartmentNotFoundException, SeatNotCreatedException {
        Token existingToken = getAndValidateToken(token);
        User user = existingToken.getUserTokens();

        if(user.getUserRole()!= UserRole.ADMIN) {
            throw new UnauthorizedUserException("User is not authorized to add seats");
        }

        Train train = trainRepository.findByTrainNumber(seatDto.getTrainNumber())
                .orElseThrow(() -> new TrainNotFoundException("Train not found"));

        Compartment compartment = compartmentRepository.findByCompartmentNumber(seatDto.getCompartment())
                .orElseThrow(() -> new CompartmentNotFoundException("Compartment not found"));

        boolean isFoundCompartment = train.getCompartments()
                        .stream()
                .anyMatch(cmp -> cmp.getCompartmentNumber().equals(seatDto.getCompartment()));

        if(!isFoundCompartment) {
            throw new CompartmentNotFoundException("train does not contain this compartment");
        }

        compartment.setCompartmentSeats(Optional.ofNullable(compartment.getCompartmentSeats())
                .orElse(new ArrayList<>()));

        if(compartment.getCompartmentSeats().size() > compartment.getTotalSeats()) {
            throw new SeatNotCreatedException("compartment limit is reached");
        }

        Seat seat = new Seat();
        seat.setSeatNumber(seatDto.getSeatNumber());
        seat.setWindowSeat(seatDto.getIsWindowSeat().equalsIgnoreCase("YES"));
        seat.setSeatStatus(SeatStatus.valueOf(seatDto.getSeatStatus().toUpperCase()));
        seat.setSeatType(SeatType.valueOf(seatDto.getSeatType().toUpperCase()));

        compartment.getCompartmentSeats().add(seat);
        seat.setCompartment(compartment);
        seatRepository.save(seat);
        compartmentRepository.save(compartment);

        return String.format("Seat added successfully in the compartment: %s for the train: %s",
                seat.getCompartment().getCompartmentNumber(),
                seat.getCompartment().getTrain().getTrainNumber());
    }

    private Token getAndValidateToken(String token) throws TokenNotFoundException, InvalidTokenException {
        Token existingToken = tokenRepository.findByTokenValue(token)
                .orElseThrow(() -> new TokenNotFoundException("Token not found"));

        if(existingToken.isDeleted() || existingToken.getTokenValidity().before(new Date())) {
            throw new InvalidTokenException("Either token is deleted or expired");
        }
        return existingToken;
    }
}
