package com.social.connectify.controllers;

import com.social.connectify.dto.GroupCreationDto;
import com.social.connectify.dto.RespondGroupRequestDto;
import com.social.connectify.exceptions.*;
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

    @PostMapping("/join-group/{groupId}")
    public ResponseEntity<String> joinGroup(@RequestHeader("Authorization") String token, @PathVariable("groupId") Long groupId) {
        try {
            String response = groupService.joinGroup(token, groupId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (InvalidTokenException invalidTokenException) {
            return new ResponseEntity<>(invalidTokenException.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (GroupNotFoundException groupNotFoundException) {
            return new ResponseEntity<>(groupNotFoundException.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/respond-request")
    public ResponseEntity<String> respondToGroupRequest(@RequestHeader("Authorization") String token,
                                                        @RequestBody RespondGroupRequestDto respondGroupRequestDto) {
        try {
            String response = groupService.respondRequest(token, respondGroupRequestDto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (InvalidTokenException | UnauthorizedUserException securityException) {
            return new ResponseEntity<>(securityException.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (GroupMembershipNotFoundException membershipNotFoundException) {
            return new ResponseEntity<>(membershipNotFoundException.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException illegalArgumentException) {
            return new ResponseEntity<>(illegalArgumentException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
