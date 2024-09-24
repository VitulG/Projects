package com.social.connectify.validations;

import com.social.connectify.dto.GroupCreationException;
import org.springframework.stereotype.Component;

@Component
public class GroupCreationValidator {
    // Implement validation logic here
    public void validateGroupCreationDetails(String name, String description) throws GroupCreationException {
        // Add validation logic
        if(name == null || name.length() < 3 || name.length() > 100) {
            throw new GroupCreationException("group name must be between 3 and 100 characters");
        }

        if(description == null || description.length() < 5 || description.length() > 256) {
            throw new GroupCreationException("group description must be between 5 and 256 characters");
        }
    }
}
