package com.social.connectify.services.MessageService;

import com.social.connectify.dto.SendMessageRequestDto;
import com.social.connectify.exceptions.GroupNotFoundException;
import com.social.connectify.exceptions.InvalidTokenException;
import com.social.connectify.exceptions.UserNotFoundException;
import com.social.connectify.models.*;
import com.social.connectify.repositories.GroupRepository;
import com.social.connectify.repositories.MessageRepository;
import com.social.connectify.repositories.TokenRepository;
import com.social.connectify.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class MessageServiceImpl implements MessageService {
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final GroupRepository groupRepository;

    @Autowired
    public MessageServiceImpl(TokenRepository tokenRepository, UserRepository userRepository,
                              MessageRepository messageRepository, GroupRepository groupRepository) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.groupRepository = groupRepository;
    }

    @Override
    public String sendMessage(SendMessageRequestDto sendMessageRequestDto, String token)
            throws InvalidTokenException, UserNotFoundException, GroupNotFoundException {
        User sender = validateTokenAndGetUser(token);
        User recipientUser = findRecipientByEmail(sendMessageRequestDto.getRecipientEmailId());

        Message message = createMessage(sender, recipientUser, sendMessageRequestDto);

        messageRepository.save(message);
        userRepository.save(recipientUser);
        return "message sent";
    }

    private User validateTokenAndGetUser(String token) throws InvalidTokenException {
        Optional<Token> optionalToken = tokenRepository.findByToken(token);
        if(optionalToken.isEmpty()) {
            throw new InvalidTokenException("token not found");
        }
        Token existingToken = optionalToken.get();
        if(!existingToken.isActive() || existingToken.getExpireAt().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("either the token is not active or expired");
        }
        return existingToken.getUser();
    }

    private User findRecipientByEmail(String email) throws UserNotFoundException {
        Optional<User> recipient = userRepository.findUserByEmail(email);
        if(recipient.isEmpty()) {
            throw new UserNotFoundException("User with the provided email id not found");
        }
        User recipientUser = recipient.get();
        if(recipientUser.isDeleted()) {
            throw new UserNotFoundException("user is deleted or not active");
        }
        return recipientUser;
    }

    private Message createMessage(User sender, User recipient, SendMessageRequestDto sendMessageRequestDto) throws GroupNotFoundException {
        Message message = new Message();
        message.setContent(sendMessageRequestDto.getMessage());
        message.setRead(false);
        message.setCreatedAt(LocalDateTime.now());

        message.setSender(sender);

        if(message.getReceivers() == null) {
            message.setReceivers(new ArrayList<>());
        }
        message.getReceivers().add(recipient);

        // Handle Images
        if(sendMessageRequestDto.getImageUrl() != null) {
            Image image = new Image();
            image.setImageUrl(sendMessageRequestDto.getImageUrl());
            image.setMessage(message);
            if(message.getImages() == null) {
                message.setImages(new ArrayList<>());
            }
            message.getImages().add(image);
        }

        // Handle Videos
        if(sendMessageRequestDto.getVideoUrl() != null) {
            Video video = new Video();
            video.setVideoLink(sendMessageRequestDto.getVideoUrl());
            video.setMessage(message);
            if(message.getVideos() == null) {
                message.setVideos(new ArrayList<>());
            }
            message.getVideos().add(video);
        }

        // Handle Groups
        if(sendMessageRequestDto.getGroupName() != null) {
            Optional<Group> recipientGroup = groupRepository.findByGroupName(sendMessageRequestDto.getGroupName());
            if(recipientGroup.isEmpty()) {
                throw new GroupNotFoundException("Group not found");
            }
            if(message.getGroups() == null) {
                message.setGroups(new ArrayList<>());
            }
            message.getGroups().add(recipientGroup.get());
        }
        return message;
    }
}
