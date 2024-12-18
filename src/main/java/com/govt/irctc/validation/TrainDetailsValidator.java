package com.govt.irctc.validation;

import com.govt.irctc.enums.TrainType;
import org.springframework.stereotype.Component;

@Component
public class TrainDetailsValidator {
    public boolean validateTrainType(String type) {
        return type.equalsIgnoreCase(TrainType.EXPRESS.toString()) || type.equalsIgnoreCase(TrainType.LOCAL.toString())
                || type.equalsIgnoreCase(TrainType.PASSENGER.toString()) || type.equalsIgnoreCase(TrainType.SUPER_FAST.toString());
    }

    public boolean validateTrainNumber(Long number) {
        if(number == null){
            return false;
        }
        return number.toString().length() == 6;
    }
}
