package com.govt.irctc.service.platformservice;

import com.govt.irctc.dto.PlatformDetailsDto;
import com.govt.irctc.enums.PlatformStatus;
import com.govt.irctc.enums.UserRole;
import com.govt.irctc.exceptions.PlatformExceptions.PlatformCreationException;
import com.govt.irctc.exceptions.SecurityExceptions.InvalidTokenException;
import com.govt.irctc.exceptions.SecurityExceptions.TokenNotFoundException;
import com.govt.irctc.exceptions.SecurityExceptions.UnauthorizedUserException;
import com.govt.irctc.exceptions.StationException.StationNotFoundException;
import com.govt.irctc.exceptions.TrainExceptions.TrainNotFoundException;
import com.govt.irctc.model.*;
import com.govt.irctc.repository.PlatformRepository;
import com.govt.irctc.repository.StationRepository;
import com.govt.irctc.repository.TokenRepository;
import com.govt.irctc.repository.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;

@Service
public class PlatformServiceImpl implements PlatformService {
    private final TrainRepository trainRepository;
    private final StationRepository stationRepository;
    private final TokenRepository tokenRepository;
    private final PlatformRepository platformRepository;

    @Autowired
    public PlatformServiceImpl(TrainRepository trainRepository, StationRepository stationRepository, TokenRepository tokenRepository,
                               PlatformRepository platformRepository) {
        this.trainRepository = trainRepository;
        this.stationRepository = stationRepository;
        this.tokenRepository = tokenRepository;
        this.platformRepository = platformRepository;
    }

    @Override
    public String addPlatform(PlatformDetailsDto detailsDto, String token) throws TokenNotFoundException, InvalidTokenException, UnauthorizedUserException, StationNotFoundException, TrainNotFoundException, PlatformCreationException {
        Token existingToken = getAndValidateToken(token);

        User admin = existingToken.getUserTokens();

        if(admin.getUserRole()!= UserRole.ADMIN) {
            throw new UnauthorizedUserException("User is not authorized to add platforms");
        }

        Station station = stationRepository.findByStationName(detailsDto.getStationName())
                .orElseThrow(() -> new StationNotFoundException("Station with the name: "+detailsDto.getStationName()+" not found"));

        Train train = trainRepository.findByTrainNumber(detailsDto.getTrainNumber())
                .orElseThrow(() -> new TrainNotFoundException("Train with the number: "+detailsDto.getTrainNumber()+" not found"));

        if (platformRepository.existsByPlatformNumberAndStation(detailsDto.getPlatformNumber(), station)) {
            throw new PlatformCreationException("Platform number " + detailsDto.getPlatformNumber() + " already exists for station " + station.getStationName());
        }

        // validate platform details here
        Platform newPlatform = new Platform();
        newPlatform.setPlatformNumber(detailsDto.getPlatformNumber());
        newPlatform.setPlatformStatus(PlatformStatus.valueOf(detailsDto.getPlatformStatus().toUpperCase()));
        newPlatform.setStation(station);

        if(newPlatform.getTrains() == null) {
            newPlatform.setTrains(new ArrayList<>());
        }
        newPlatform.getTrains().add(train);
        train.setPlatform(newPlatform);

        platformRepository.save(newPlatform);

        return "platform has been added for the station: "+newPlatform.getStation().getStationName();

    }
    private Token getAndValidateToken(String token) throws TokenNotFoundException, InvalidTokenException {
        Token existingToken = tokenRepository.findByTokenValue(token)
                .orElseThrow(() -> new TokenNotFoundException("Token not found"));

        if(existingToken.isDeleted() || existingToken.getTokenValidity().before(new Date())) {
            throw new InvalidTokenException("Either token is deleted or expired");
        }
        return existingToken;
    }
}
