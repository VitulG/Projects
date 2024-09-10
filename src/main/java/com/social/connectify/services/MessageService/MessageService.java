package com.social.connectify.services.MessageService;

import com.social.connectify.dto.SendMessageRequestDto;
import com.social.connectify.exceptions.GroupNotFoundException;
import com.social.connectify.exceptions.InvalidTokenException;
import com.social.connectify.exceptions.UserNotFoundException;

public interface MessageService {
    String sendMessage(SendMessageRequestDto sendMessageRequestDto, String token) throws InvalidTokenException,
            UserNotFoundException, GroupNotFoundException;
}
