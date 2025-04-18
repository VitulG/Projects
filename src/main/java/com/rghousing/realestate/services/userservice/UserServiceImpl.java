package com.rghousing.realestate.services.userservice;

import com.rghousing.realestate.dto.UserDto;
import com.rghousing.realestate.entities.User;
import com.rghousing.realestate.exceptions.UserCreationException;
import com.rghousing.realestate.repositories.UserRepository;
import com.rghousing.realestate.validations.UserDetailsValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserDetailsValidator userDetailsValidator;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserDetailsValidator userDetailsValidator, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.userDetailsValidator = userDetailsValidator;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public String signupUser(UserDto userDto) throws UserCreationException {

        if(userRepository.existsUserByUserEmail(userDto.getEmail())) {
            throw new UserCreationException("user already exists");
        }

        userDetailsValidator.validateUserSignupDetails(userDto);

        User user = new User();
        user.setUserName(userDto.getUsername());
        user.setUserEmail(userDto.getEmail());
        user.setUserPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        user.setUserPhoneNumber(userDto.getPhoneNumber());
        user.setUserAddress(userDto.getAddress());

        userRepository.save(user);
        return "Welcome to RgHousing.com " + user.getUserName();
    }
}
