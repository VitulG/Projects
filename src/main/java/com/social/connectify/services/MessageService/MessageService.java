package com.social.connectify.services.MessageService;

import com.social.connectify.dto.GroupMessageDto;
import com.social.connectify.dto.ReceivedMessageDto;
import com.social.connectify.dto.SendMessageRequestDto;
import com.social.connectify.dto.SentMessageDto;
import com.social.connectify.exceptions.*;

import java.util.List;

public interface MessageService {
    String sendMessage(SendMessageRequestDto sendMessageRequestDto, String token) throws InvalidTokenException,
            UserNotFoundException, GroupNotFoundException;
    List<SentMessageDto> getSentMessages(String token) throws InvalidTokenException, MessageNotFoundException;
    void readMessage(Long messageId, String token) throws InvalidTokenException, MessageNotFoundException;
    List<ReceivedMessageDto> getReceivedMessages(String token) throws InvalidTokenException, MessageNotFoundException;
    List<GroupMessageDto> getMessagesFromAGroup(String token, Long groupId) throws InvalidTokenException, GroupNotFoundException,
            UserNotInGroupException;
    String deleteMessage(String token, Long messageId) throws InvalidTokenException, MessageNotFoundException, UnauthorizedUserException;
}
