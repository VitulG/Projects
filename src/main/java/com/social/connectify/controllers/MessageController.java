package com.social.connectify.controllers;

import com.social.connectify.dto.ReceivedMessageDto;
import com.social.connectify.dto.SendMessageRequestDto;
import com.social.connectify.dto.SentMessageDto;
import com.social.connectify.exceptions.GroupNotFoundException;
import com.social.connectify.exceptions.InvalidTokenException;
import com.social.connectify.exceptions.MessageNotFoundException;
import com.social.connectify.exceptions.UserNotFoundException;
import com.social.connectify.services.MessageService.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {
    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/send-message")
    public ResponseEntity<String> sendMessage(@RequestBody SendMessageRequestDto sendMessageRequestDto,
                                      @RequestHeader("Authorization") String token) {
        try {
            String response = messageService.sendMessage(sendMessageRequestDto, token);
            return ResponseEntity.ok(response);
        } catch (InvalidTokenException invalidTokenException) {
            return new ResponseEntity<>(invalidTokenException.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (UserNotFoundException | GroupNotFoundException notFoundException) {
            return new ResponseEntity<>(notFoundException.getMessage(), HttpStatus.NOT_FOUND);
        } catch(Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/sent-messages")
    public ResponseEntity<List<SentMessageDto>> getSentMessages(@RequestHeader("Authorization") String token) {
        try {
            List<SentMessageDto> sentMessages = messageService.getSentMessages(token);
            return ResponseEntity.ok(sentMessages);
        }catch (InvalidTokenException invalidTokenException) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
        } catch (MessageNotFoundException messageNotFoundException) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/received-messages")
    public ResponseEntity<List<ReceivedMessageDto>> getReceivedMessages(@RequestHeader("Authorization") String token) {
        try {
            List<ReceivedMessageDto> receivedMessages = messageService.getReceivedMessages(token);
            return ResponseEntity.ok(receivedMessages);
        } catch (InvalidTokenException invalidTokenException) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
        } catch (MessageNotFoundException messageNotFoundException) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PatchMapping("/read-message/{messageId}/read")
    public ResponseEntity<Void> markMessageAsRead(@PathVariable Long messageId,
                                      @RequestHeader("Authorization") String token) {
        try {
            messageService.readMessage(messageId, token);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (InvalidTokenException invalidTokenException) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (MessageNotFoundException messageNotFoundException) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
