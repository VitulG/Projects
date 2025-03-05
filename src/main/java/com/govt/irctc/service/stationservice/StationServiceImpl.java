package com.govt.irctc.service.stationservice;

import com.govt.irctc.dto.StationDetailsDto;
import com.govt.irctc.enums.StationStatus;
import com.govt.irctc.enums.UserRole;
import com.govt.irctc.exceptions.CityExceptions.CityNotFoundException;
import com.govt.irctc.exceptions.RouteException.RouteNotFoundException;
import com.govt.irctc.exceptions.SecurityExceptions.InvalidTokenException;
import com.govt.irctc.exceptions.SecurityExceptions.TokenNotFoundException;
import com.govt.irctc.exceptions.SecurityExceptions.UnauthorizedUserException;
import com.govt.irctc.exceptions.TrainExceptions.TrainNotFoundException;
import com.govt.irctc.model.*;
import com.govt.irctc.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;

@Service
public class StationServiceImpl implements StationService {
    private final StationRepository stationRepository;
    private final TrainRepository trainRepository;
    private final RouteRepository routeRepository;
    private final TokenRepository tokenRepository;
    private final CityRepository cityRepository;

    @Autowired
    public StationServiceImpl(StationRepository stationRepository, TrainRepository trainRepository, RouteRepository routeRepository,
                              TokenRepository tokenRepository, CityRepository cityRepository) {
        this.stationRepository = stationRepository;
        this.trainRepository = trainRepository;
        this.routeRepository = routeRepository;
        this.tokenRepository = tokenRepository;
        this.cityRepository = cityRepository;
    }

    @Override
    @Transactional
    public String addStation(StationDetailsDto detailsDto, String token) throws TokenNotFoundException, InvalidTokenException, UnauthorizedUserException, TrainNotFoundException, RouteNotFoundException, CityNotFoundException {
        Token existingToken = getAndValidateToken(token);

        User admin = existingToken.getUserTokens();

        if(admin.getUserRole() != UserRole.ADMIN) {
            throw new UnauthorizedUserException("User is not authorized to add stations");
        }

        Station newStation = new Station();
        newStation.setStationName(detailsDto.getStationName());
        City city = cityRepository.findByCityName(detailsDto.getCity())
                .orElseThrow(() -> new CityNotFoundException("City not found"));
        newStation.setCity(city);

        newStation.setStationStatus(StationStatus.valueOf(detailsDto.getStationStatus().toUpperCase()));

        if(newStation.getTrains() == null) {
            newStation.setTrains(new ArrayList<>());
        }

        for(Long trainNumber : detailsDto.getTrains()) {
            Train train = trainRepository.findByTrainNumber(trainNumber)
                    .orElseThrow(() -> new TrainNotFoundException("Train not found for train number " + trainNumber));
            newStation.getTrains().add(train);
        }

        if (newStation.getStationRoutes() == null) {
            newStation.setStationRoutes(new ArrayList<>());
        }

        for(String routeNumber : detailsDto.getRoutes()) {
            Route route = routeRepository.findByRouteNumber(routeNumber)
                    .orElseThrow(() -> new RouteNotFoundException("Route Number: " + routeNumber+" not found"));
            newStation.getStationRoutes().add(route);
            route.getStations().add(newStation);
        }
        stationRepository.save(newStation);
        city.setStation(newStation);

        return "Station added successfully";
    }

    private Token getAndValidateToken(String token) throws TokenNotFoundException, InvalidTokenException {
        // Implementation to get and validate token
        Token existingToken = tokenRepository.findByTokenValue(token)
                .orElseThrow(() -> new TokenNotFoundException("Token not found"));

        if (existingToken.isDeleted() || existingToken.getTokenValidity().before(new Date())) {
            throw new InvalidTokenException("Token is expired or deleted");
        }

        return existingToken;
    }
}
