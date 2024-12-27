package com.govt.irctc.service.platformservice;

import com.govt.irctc.dto.PlatformDetailsDto;
import com.govt.irctc.exceptions.PlatformExceptions.PlatformCreationException;
import com.govt.irctc.exceptions.SecurityExceptions.InvalidTokenException;
import com.govt.irctc.exceptions.SecurityExceptions.TokenNotFoundException;
import com.govt.irctc.exceptions.SecurityExceptions.UnauthorizedUserException;
import com.govt.irctc.exceptions.StationException.StationNotFoundException;
import com.govt.irctc.exceptions.TrainExceptions.TrainNotFoundException;

public interface PlatformService {
    public String addPlatform(PlatformDetailsDto detailsDto, String token) throws TokenNotFoundException, InvalidTokenException, UnauthorizedUserException, StationNotFoundException, TrainNotFoundException, PlatformCreationException;
}
