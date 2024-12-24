package com.govt.irctc.service.seatservice;


import com.govt.irctc.dto.SeatDto;
import com.govt.irctc.dto.ShowSeatDto;
import com.govt.irctc.exceptions.CompartmentException.CompartmentNotFoundException;
import com.govt.irctc.exceptions.SeatExceptions.SeatNotCreatedException;
import com.govt.irctc.exceptions.SeatExceptions.SeatsNotFoundException;
import com.govt.irctc.exceptions.SecurityExceptions.InvalidTokenException;
import com.govt.irctc.exceptions.SecurityExceptions.TokenNotFoundException;
import com.govt.irctc.exceptions.SecurityExceptions.UnauthorizedUserException;
import com.govt.irctc.exceptions.TrainExceptions.TrainNotFoundException;

public interface SeatService {
    public String addSeats(SeatDto seatDto, String token)
            throws TrainNotFoundException, InvalidTokenException, UnauthorizedUserException, TokenNotFoundException, CompartmentNotFoundException, SeatNotCreatedException;

}
