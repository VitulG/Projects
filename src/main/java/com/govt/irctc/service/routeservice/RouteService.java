package com.govt.irctc.service.routeservice;

import com.govt.irctc.dto.RouteDetailsDto;
import com.govt.irctc.exceptions.RouteException.RouteCreationException;
import com.govt.irctc.exceptions.SecurityExceptions.InvalidTokenException;
import com.govt.irctc.exceptions.SecurityExceptions.TokenNotFoundException;
import com.govt.irctc.exceptions.SecurityExceptions.UnauthorizedUserException;
import com.govt.irctc.exceptions.StationException.StationNotFoundException;
import com.govt.irctc.exceptions.TrainExceptions.TrainNotFoundException;

public interface RouteService {
    public String addRoute(RouteDetailsDto routeDetailsDto, String token) throws TokenNotFoundException, InvalidTokenException, UnauthorizedUserException, TrainNotFoundException, RouteCreationException;
}
