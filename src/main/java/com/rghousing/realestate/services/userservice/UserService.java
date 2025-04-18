package com.rghousing.realestate.services.userservice;

import com.rghousing.realestate.dto.UserDto;
import com.rghousing.realestate.exceptions.UserCreationException;

public interface UserService {
    public String signupUser(UserDto user) throws UserCreationException;
}
