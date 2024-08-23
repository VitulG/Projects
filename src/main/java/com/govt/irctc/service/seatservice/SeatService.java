package com.govt.irctc.service.seatservice;

import com.govt.irctc.advice.LoginAdvice.InvalidTokenException;
import com.govt.irctc.advice.SeatAdvice.SeatsNotFoundException;
import com.govt.irctc.advice.TrainAdvice.TrainNotFoundException;
import com.govt.irctc.advice.UnauthorizedUserException;
import com.govt.irctc.dto.SeatDto;
import com.govt.irctc.dto.ShowSeatDto;

public interface SeatService {
    public String addSeats(SeatDto seatDto, String token)
            throws TrainNotFoundException, InvalidTokenException, UnauthorizedUserException;
    public ShowSeatDto showSeats(Long trainNumber, String token) throws InvalidTokenException,
            TrainNotFoundException, SeatsNotFoundException;
    public String updateSeatsByTrainNumber(Long trainNumber, SeatDto seatDto, String token) throws InvalidTokenException, TrainNotFoundException, SeatsNotFoundException, UnauthorizedUserException;
    public String deleteSeatsByTrainNumber(Long trainNumber, String token) throws InvalidTokenException, TrainNotFoundException, SeatsNotFoundException, UnauthorizedUserException;
}
