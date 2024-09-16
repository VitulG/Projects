package com.social.connectify.services.MessageService;

import com.social.connectify.dto.ReceivedMessageDto;
import com.social.connectify.dto.SendMessageRequestDto;
import com.social.connectify.dto.SentMessageDto;
import com.social.connectify.exceptions.GroupNotFoundException;
import com.social.connectify.exceptions.InvalidTokenException;
import com.social.connectify.exceptions.MessageNotFoundException;
import com.social.connectify.exceptions.UserNotFoundException;

import java.util.List;

public interface MessageService {
    String sendMessage(SendMessageRequestDto sendMessageRequestDto, String token) throws InvalidTokenException,
            UserNotFoundException, GroupNotFoundException;
    List<SentMessageDto> getSentMessages(String token) throws InvalidTokenException, MessageNotFoundException;
    void readMessage(Long messageId, String token) throws InvalidTokenException, MessageNotFoundException;
    List<ReceivedMessageDto> getReceivedMessages(String token) throws InvalidTokenException, MessageNotFoundException;
}
