package com.govt.irctc.validation;

import com.govt.irctc.enums.CompartmentType;
import com.govt.irctc.exceptions.CompartmentException.CompartmentCreationException;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class CompartmentDetailsValidator {
    public void validateCompartmentNumber(String compartmentNumber) throws CompartmentCreationException {
        if(compartmentNumber == null || compartmentNumber.length() < 2) {
            throw new CompartmentCreationException("Compartment number should be a 3-character string.");
        }
    }

    public void validateNumberOfSeats(int numberOfSeats) throws CompartmentCreationException {
        if(numberOfSeats <= 0) {
            throw new CompartmentCreationException("Number of seats should be a positive integer.");
        }
    }

    public void validateCompartmentPrice(double price) throws CompartmentCreationException {
        if(price <= 0) {
            throw new CompartmentCreationException("Price should be a positive number.");
        }
    }

    public void validateCompartmentType(String compartmentType) throws CompartmentCreationException {
        if(compartmentType == null || compartmentType.isEmpty()) {
            throw new CompartmentCreationException("Compartment type should not be empty.");
        }
        boolean isMatched = Arrays.stream(CompartmentType.values())
                .anyMatch(compartmentTypes -> compartmentTypes.toString()
                        .equalsIgnoreCase(compartmentType));

        if(!isMatched) {
            throw new CompartmentCreationException("Invalid compartment type");
        }
    }
}
