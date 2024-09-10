package com.social.connectify.controllers;

import com.social.connectify.dto.SendMessageRequestDto;
import com.social.connectify.exceptions.GroupNotFoundException;
import com.social.connectify.exceptions.InvalidTokenException;
import com.social.connectify.exceptions.UserNotFoundException;
import com.social.connectify.services.MessageService.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
