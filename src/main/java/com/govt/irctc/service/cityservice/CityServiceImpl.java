package com.govt.irctc.service.cityservice;

import com.govt.irctc.dto.CityDetailsDto;
import com.govt.irctc.enums.UserRole;
import com.govt.irctc.exceptions.SecurityExceptions.InvalidTokenException;
import com.govt.irctc.exceptions.SecurityExceptions.TokenNotFoundException;
import com.govt.irctc.exceptions.SecurityExceptions.UnauthorizedUserException;
import com.govt.irctc.model.City;
import com.govt.irctc.model.Station;
import com.govt.irctc.model.Token;
import com.govt.irctc.model.User;
import com.govt.irctc.repository.CityRepository;
import com.govt.irctc.repository.StationRepository;
import com.govt.irctc.repository.TokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class CityServiceImpl implements CityService {
    private final TokenRepository tokenRepository;
    private final CityRepository cityRepository;
    private final StationRepository stationRepository;

    @Autowired
    public CityServiceImpl(TokenRepository tokenRepository, CityRepository cityRepository, StationRepository stationRepository) {
        this.tokenRepository = tokenRepository;
        this.cityRepository = cityRepository;
        this.stationRepository = stationRepository;
    }

    @Override
    @Transactional
    public String addCity(CityDetailsDto detailsDto, String token) throws TokenNotFoundException, InvalidTokenException, UnauthorizedUserException {
        Token existingToken = getAndValidateToken(token);

        User user = existingToken.getUserTokens();

        if(user.getUserRole() != UserRole.ADMIN) {
            throw new UnauthorizedUserException("User is not authorized to add city");
        }

        // validate the city details here
        City city = new City();
        city.setCityName(detailsDto.getCityName());
        city.setState(detailsDto.getState());
        city.setCountry(detailsDto.getCountry());
        city.setZipCode(detailsDto.getZipCode());
        city.setLatitude(detailsDto.getLatitude());
        city.setLongitude(detailsDto.getLongitude());

        Optional<Station> optionalStation = stationRepository.findByStationName(detailsDto.getStationName());
        optionalStation.ifPresent(city::setStation);

        cityRepository.save(city);

        return "City added successfully";
    }

    private Token getAndValidateToken(String token) throws TokenNotFoundException, InvalidTokenException {
        Token existingToken =  tokenRepository.findByTokenValue(token)
               .orElseThrow(() -> new TokenNotFoundException("Token not found"));

        if(existingToken.isDeleted() || existingToken.getTokenValidity().before(new Date())) {
            throw new InvalidTokenException("Either token is deleted or expired");
        }
        return existingToken;
    }
}
