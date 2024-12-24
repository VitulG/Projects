package com.govt.irctc.service.compartmentservice;

import com.govt.irctc.dto.CompartmentDetailsDto;
import com.govt.irctc.exceptions.CompartmentException.CompartmentCreationException;
import com.govt.irctc.exceptions.SecurityExceptions.InvalidTokenException;
import com.govt.irctc.exceptions.SecurityExceptions.TokenNotFoundException;
import com.govt.irctc.exceptions.SecurityExceptions.UnauthorizedUserException;
import com.govt.irctc.exceptions.TrainExceptions.TrainNotFoundException;

public interface CompartmentService {
    public String addCompartment(CompartmentDetailsDto compartmentDetailsDto, String token) throws TokenNotFoundException, UnauthorizedUserException, InvalidTokenException, CompartmentCreationException, TrainNotFoundException;
}
