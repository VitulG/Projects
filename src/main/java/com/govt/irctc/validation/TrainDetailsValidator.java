package com.govt.irctc.validation;

import com.govt.irctc.enums.TrainType;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class TrainDetailsValidator {
    public boolean validateTrainType(String type) {
        return Arrays.stream(TrainType.values())
                .anyMatch(trainType -> trainType.name().equalsIgnoreCase(type));
    }

    public boolean validateTrainNumber(Long number) {
        if(number == null){
            return false;
        }
        return number.toString().length() == 6;
    }
}
