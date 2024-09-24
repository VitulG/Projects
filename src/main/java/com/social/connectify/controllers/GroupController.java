package com.social.connectify.controllers;

import com.social.connectify.dto.GroupCreationDto;
import com.social.connectify.dto.GroupCreationException;
import com.social.connectify.exceptions.InvalidTokenException;
import com.social.connectify.services.GroupService.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/groups")
public class GroupController {
    private final GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping("/create-group")
    public ResponseEntity<String> createGroup(@RequestHeader("Authorization") String token,
                                              @RequestBody GroupCreationDto creationDto) {
        try {
            String response = groupService.createGroup(token, creationDto);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (InvalidTokenException invalidTokenException) {
            return new ResponseEntity<>(invalidTokenException.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (GroupCreationException groupCreationException) {
            return new ResponseEntity<>(groupCreationException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
