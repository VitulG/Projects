package com.govt.irctc.service.stationservice;

import com.govt.irctc.dto.StationDetailsDto;
import com.govt.irctc.exceptions.RouteException.RouteNotFoundException;
import com.govt.irctc.exceptions.SecurityExceptions.InvalidTokenException;
import com.govt.irctc.exceptions.SecurityExceptions.TokenNotFoundException;
import com.govt.irctc.exceptions.SecurityExceptions.UnauthorizedUserException;
import com.govt.irctc.exceptions.TrainExceptions.TrainNotFoundException;

public interface StationService {
    public String addStation(StationDetailsDto detailsDto, String token) throws TokenNotFoundException, InvalidTokenException, UnauthorizedUserException, TrainNotFoundException, RouteNotFoundException;
}
