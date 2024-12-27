package com.govt.irctc.service.compartmentservice;

import com.govt.irctc.dto.CompartmentDetailsDto;
import com.govt.irctc.enums.CompartmentType;
import com.govt.irctc.enums.UserRole;
import com.govt.irctc.exceptions.CompartmentException.CompartmentCreationException;
import com.govt.irctc.exceptions.SecurityExceptions.InvalidTokenException;
import com.govt.irctc.exceptions.SecurityExceptions.TokenNotFoundException;
import com.govt.irctc.exceptions.SecurityExceptions.UnauthorizedUserException;
import com.govt.irctc.exceptions.TrainExceptions.TrainNotFoundException;
import com.govt.irctc.model.Compartment;
import com.govt.irctc.model.Token;
import com.govt.irctc.model.Train;
import com.govt.irctc.model.User;
import com.govt.irctc.repository.CompartmentRepository;
import com.govt.irctc.repository.TokenRepository;
import com.govt.irctc.repository.TrainRepository;
import com.govt.irctc.validation.CompartmentDetailsValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class CompartmentServiceImpl implements CompartmentService {
    private final TrainRepository trainRepository;
    private final CompartmentRepository compartmentRepository;
    private final TokenRepository tokenRepository;
    private final CompartmentDetailsValidator compartmentDetailsValidator;

    @Autowired
    public CompartmentServiceImpl(TrainRepository trainRepository, CompartmentRepository compartmentRepository, TokenRepository tokenRepository,
                                  CompartmentDetailsValidator compartmentDetailsValidator) {
        this.trainRepository = trainRepository;
        this.compartmentRepository = compartmentRepository;
        this.tokenRepository = tokenRepository;
        this.compartmentDetailsValidator = compartmentDetailsValidator;
    }

    @Override
    public String addCompartment(CompartmentDetailsDto compartmentDetailsDto, String token) throws TokenNotFoundException, UnauthorizedUserException,
            InvalidTokenException, CompartmentCreationException, TrainNotFoundException {
        Token existingToken = getAndValidateToken(token);
        User user = existingToken.getUserTokens();

        if(user.getUserRole() != UserRole.ADMIN) {
            throw new UnauthorizedUserException("User is not authorized to add compartments");
        }

        validateCompartmentDetails(compartmentDetailsDto);

        Train existingTrain = trainRepository.findByTrainNumber(compartmentDetailsDto.getTrainNumber())
                .orElseThrow(() -> new TrainNotFoundException("Train not found"));

        Optional<Compartment> optionalCompartment = compartmentRepository
                .findByCompartmentNumber(compartmentDetailsDto.getCompartmentNumber());

        if(optionalCompartment.isPresent()) {
            throw new CompartmentCreationException("Compartment is already exist");
        }

        Compartment compartment = new Compartment();
        compartment.setCompartmentNumber(compartmentDetailsDto.getCompartmentNumber());
        compartment.setCompartmentType(CompartmentType.valueOf(compartmentDetailsDto.getCompartmentType().toUpperCase()));
        compartment.setTotalSeats(compartmentDetailsDto.getNumberOfSeats());
        compartment.setTrain(existingTrain);

        compartmentRepository.save(compartment);

        return "compartment added for the train: "+compartment.getTrain().getTrainNumber();
    }

    private void validateCompartmentDetails(CompartmentDetailsDto compartmentDetailsDto) throws CompartmentCreationException {
        // Validation logic here
        compartmentDetailsValidator.validateCompartmentNumber(compartmentDetailsDto.getCompartmentNumber());
        compartmentDetailsValidator.validateCompartmentType(compartmentDetailsDto.getCompartmentType());
        compartmentDetailsValidator.validateCompartmentPrice(compartmentDetailsDto.getPricePerSeat());
        compartmentDetailsValidator.validateNumberOfSeats(compartmentDetailsDto.getNumberOfSeats());
    }

    private Token getAndValidateToken(String token) throws TokenNotFoundException, InvalidTokenException {
        Token existingToken = tokenRepository.findByTokenValue(token)
               .orElseThrow(() -> new TokenNotFoundException("Token not found"));

        if (existingToken.isDeleted() || existingToken.getTokenValidity().before(new Date())) {
            throw new InvalidTokenException("Token is expired or invalid");
        }
        return existingToken;
    }
}
