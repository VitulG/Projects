package com.govt.irctc.service.routeservice;

import com.govt.irctc.dto.RouteDetailsDto;
import com.govt.irctc.enums.RouteStatus;
import com.govt.irctc.enums.UserRole;
import com.govt.irctc.exceptions.RouteException.RouteCreationException;
import com.govt.irctc.exceptions.SecurityExceptions.InvalidTokenException;
import com.govt.irctc.exceptions.SecurityExceptions.TokenNotFoundException;
import com.govt.irctc.exceptions.SecurityExceptions.UnauthorizedUserException;
import com.govt.irctc.exceptions.StationException.StationNotFoundException;
import com.govt.irctc.exceptions.TrainExceptions.TrainNotFoundException;
import com.govt.irctc.model.*;
import com.govt.irctc.repository.RouteRepository;
import com.govt.irctc.repository.StationRepository;
import com.govt.irctc.repository.TokenRepository;
import com.govt.irctc.repository.TrainRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

@Service
public class RouteServiceImpl implements RouteService {
    private final TokenRepository tokenRepository;
    private final RouteRepository routeRepository;
    private final TrainRepository trainRepository;
    private final StationRepository stationRepository;

    @Autowired
    public RouteServiceImpl(TokenRepository tokenRepository, RouteRepository routeRepository, TrainRepository trainRepository, StationRepository stationRepository) {
        this.tokenRepository = tokenRepository;
        this.routeRepository = routeRepository;
        this.trainRepository = trainRepository;
        this.stationRepository = stationRepository;
    }

    @Override
    @Transactional
    public String addRoute(RouteDetailsDto routeDetailsDto, String token) throws TokenNotFoundException, InvalidTokenException,
            UnauthorizedUserException, TrainNotFoundException, RouteCreationException {
        Token existingToken = getAndValidateToken(token);
        User user = existingToken.getUserTokens();

        if(user.getUserRole()!= UserRole.ADMIN) {
            throw new UnauthorizedUserException("User is not authorized to add routes");
        }

        Train train = trainRepository.findByTrainNumber(routeDetailsDto.getTrainNumber())
                .orElseThrow(() -> new TrainNotFoundException("Train not found"));

        Optional<Station> optionalStation = stationRepository.findByStationName(routeDetailsDto.getStationName());

        Optional<Route> optionalRoute = routeRepository.findByRouteNumber(routeDetailsDto.getRouteNumber());

        if(optionalRoute.isPresent()) {
            throw new RouteCreationException("Route is already exist");
        }

        Route route = new Route();
        route.setRouteNumber(routeDetailsDto.getRouteNumber());
        route.setRouteStatus(RouteStatus.valueOf(routeDetailsDto.getRouteStatus().toUpperCase()));

        if(route.getTrain() == null) {
            route.setTrain(new ArrayList<>());
        }
        route.getTrain().add(train);
        train.getRoutes().add(route);

        if(optionalStation.isPresent()) {
            Station station = optionalStation.get();
            if(route.getStations() == null) {
                route.setStations(new ArrayList<>());
            }
            route.getStations().add(station);
            station.getStationRoutes().add(route);
            stationRepository.save(station);
        }

        routeRepository.save(route);
        trainRepository.save(train);

        return "Route added successfully for the train: "+train.getTrainNumber();
    }

    private Token getAndValidateToken(String token) throws TokenNotFoundException, InvalidTokenException {
        Token existingToken = tokenRepository.findByTokenValue(token)
                .orElseThrow(() -> new TokenNotFoundException("Token not found"));

        if(existingToken.isDeleted() || existingToken.getTokenValidity().before(new Date())) {
            throw new InvalidTokenException("Either the token is deleted or expired");
        }
        return existingToken;
    }
}
