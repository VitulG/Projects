package com.govt.irctc.service.cityservice;

import com.govt.irctc.dto.CityDetailsDto;
import com.govt.irctc.exceptions.SecurityExceptions.InvalidTokenException;
import com.govt.irctc.exceptions.SecurityExceptions.TokenNotFoundException;
import com.govt.irctc.exceptions.SecurityExceptions.UnauthorizedUserException;

public interface CityService {
    public String addCity(CityDetailsDto detailsDto, String token) throws TokenNotFoundException, InvalidTokenException, UnauthorizedUserException;
}
