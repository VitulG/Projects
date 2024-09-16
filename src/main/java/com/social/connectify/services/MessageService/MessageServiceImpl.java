package com.social.connectify.services.MessageService;

import com.social.connectify.dto.ReceivedMessageDto;
import com.social.connectify.dto.SendMessageRequestDto;
import com.social.connectify.dto.SentMessageDto;
import com.social.connectify.exceptions.GroupNotFoundException;
import com.social.connectify.exceptions.InvalidTokenException;
import com.social.connectify.exceptions.MessageNotFoundException;
import com.social.connectify.exceptions.UserNotFoundException;
import com.social.connectify.models.*;
import com.social.connectify.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final GroupRepository groupRepository;
    private final ImageRepository imageRepository;
    private final VideoRepository videoRepository;

    @Autowired
    public MessageServiceImpl(TokenRepository tokenRepository, UserRepository userRepository,
                              MessageRepository messageRepository, GroupRepository groupRepository, ImageRepository imageRepository,
                              VideoRepository videoRepository) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.groupRepository = groupRepository;
        this.imageRepository = imageRepository;
        this.videoRepository = videoRepository;
    }

    @Override
    public String sendMessage(SendMessageRequestDto sendMessageRequestDto, String token)
            throws InvalidTokenException, UserNotFoundException, GroupNotFoundException {
        User sender = validateTokenAndGetUser(token);
        User recipientUser = findRecipientByEmail(sendMessageRequestDto.getRecipientEmailId());

        Message message = createMessage(sender, recipientUser, sendMessageRequestDto);

        if(sender.getSentMessages() == null) {
            sender.setSentMessages(new ArrayList<>());
        }
        sender.getSentMessages().add(message);

        if(recipientUser.getReceivedMessages() == null) {
            recipientUser.setReceivedMessages(new ArrayList<>());
        }
        recipientUser.getReceivedMessages().add(message);

        messageRepository.save(message);
        return "message sent";
    }

    @Override
    public List<SentMessageDto> getSentMessages(String token) throws InvalidTokenException, MessageNotFoundException {
        User user = validateTokenAndGetUser(token);

        List<Message> sentMessages = user.getSentMessages();

        if(sentMessages == null) {
            throw new MessageNotFoundException("Messages not available at this time");
        }

        List<SentMessageDto> sentMessagesDto = new ArrayList<>();

        for(Message message : sentMessages) {
            SentMessageDto messageDto = new SentMessageDto();
            messageDto.setMessage(message.getContent());
            List<String> recipientsOfAMessage = new ArrayList<>();
            for(User recipient : message.getReceivers()) {
                recipientsOfAMessage.add(recipient.getFirstName()+" "+recipient.getLastName());
            }
            messageDto.setRecipients(recipientsOfAMessage);
            sentMessagesDto.add(messageDto);
        }
        return sentMessagesDto;
    }

    @Override
    public void readMessage(Long messageId, String token) throws InvalidTokenException, MessageNotFoundException {
        User user = validateTokenAndGetUser(token);

        Optional<Message> optionalMessage = messageRepository.findById(messageId);

        if(optionalMessage.isEmpty()) {
            throw new MessageNotFoundException("Message not available");
        }

        Message message = optionalMessage.get();
        List<User> messageReceivers = message.getReceivers();

        // Use Java 8 Streams for checking if the user is one of the recipients
        boolean isUserPresent = messageReceivers.stream()
                .anyMatch(receiver -> receiver.getEmail().equals(user.getEmail()));

        if(!isUserPresent) {
            throw new MessageNotFoundException("Message not available to this user");
        }

        // Update the status only if it isn't already read
        if (message.getMessageStatus() != MessageStatus.READ) {
            message.setMessageStatus(MessageStatus.READ);
            messageRepository.save(message);
        }
    }

    @Override
    public List<ReceivedMessageDto> getReceivedMessages(String token) throws InvalidTokenException, MessageNotFoundException {
        User user = validateTokenAndGetUser(token);

        List<Message> receivedMessages = user.getReceivedMessages();

        if(receivedMessages == null) {
            throw new MessageNotFoundException("Messages not available at this time");
        }

        // Convert messages to DTOs
        return receivedMessages.stream()
                .map(message -> {
                    ReceivedMessageDto messageDto = new ReceivedMessageDto();
                    messageDto.setMessage(message.getContent());
                    messageDto.setSender(message.getSender().getFirstName() + " " + message.getSender().getLastName());
                    return messageDto;
                })
                .collect(Collectors.toList());
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
        message.setMessageStatus(MessageStatus.SENT);
        message.setCreatedAt(LocalDateTime.now());

        message.setSender(sender);

        if(sender.getSentMessages() == null) {
            sender.setSentMessages(new ArrayList<>());
        }
        sender.getSentMessages().add(message);

        if(message.getReceivers() == null) {
            message.setReceivers(new ArrayList<>());
        }
        message.getReceivers().add(recipient);

        if(recipient.getReceivedMessages() == null) {
            recipient.setReceivedMessages(new ArrayList<>());
        }
        recipient.getReceivedMessages().add(message);

        // Handle Images
        if(sendMessageRequestDto.getImageUrl() != null) {
            Image image = new Image();
            image.setImageUrl(sendMessageRequestDto.getImageUrl());
            image.setMessage(message);
            if(message.getImages() == null) {
                message.setImages(new ArrayList<>());
            }
            message.getImages().add(image);
            imageRepository.save(image);
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
            videoRepository.save(video);
        }

        // Handle Groups
        if(sendMessageRequestDto.getGroups() != null) {
            for(String group : sendMessageRequestDto.getGroups()) {
                Optional<Group> recipientGroup = groupRepository.findByGroupName(group);
                if(recipientGroup.isEmpty()) {
                    throw new GroupNotFoundException("Group not found");
                }
                if(message.getGroups() == null) {
                    message.setGroups(new ArrayList<>());
                }
                message.getGroups().add(recipientGroup.get());
                groupRepository.save(recipientGroup.get());
            }
        }
        return message;
    }
}
