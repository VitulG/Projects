package com.social.connectify.controllers;

import com.social.connectify.dto.FriendRequestDto;
import com.social.connectify.dto.FriendshipDto;
import com.social.connectify.exceptions.FriendNotFoundException;
import com.social.connectify.exceptions.FriendshipNotFoundException;
import com.social.connectify.exceptions.InvalidTokenException;
import com.social.connectify.exceptions.UserNotFoundException;
import com.social.connectify.models.Friendship;
import com.social.connectify.services.FriendshipService.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/friend")
public class FriendController {
    private final FriendshipService friendService;

    @Autowired
    public FriendController(FriendshipService friendService) {
        this.friendService = friendService;
    }

    @PostMapping("/respond-friendRequest")
    public ResponseEntity<String> respondFriendRequest(@RequestBody FriendRequestDto friendRequestDto,
                                             @RequestHeader("Authorization") String token) {
        try {
            String response = friendService.respondFriendRequest(friendRequestDto, token);
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        } catch (InvalidTokenException invalidTokenException) {
            return new ResponseEntity<>(invalidTokenException.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (UserNotFoundException | FriendshipNotFoundException notFoundException) {
            return new ResponseEntity<>(notFoundException.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalStateException illegalStateException){
            return new ResponseEntity<>(illegalStateException.getMessage(), HttpStatus.BAD_REQUEST);
        }catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add-friend/{friendEmail}")
    public ResponseEntity<String> addFriend(@RequestHeader("Authorization") String token,
                                                 @PathVariable("friendEmail") String friendEmailId) {
        try {
            String response = friendService.addAFriend(token, friendEmailId);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (InvalidTokenException invalidTokenException) {
            return new ResponseEntity<>(invalidTokenException.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (FriendNotFoundException friendNotFoundException) {
            return new ResponseEntity<>(friendNotFoundException.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/cancel-request/{friendEmailId}")
    public ResponseEntity<String> cancelFriendRequest(@RequestHeader("Authorization") String token,
                                                      @PathVariable("friendEmailId") String friendEmailId) {
        try {
            String response = friendService.cancelFriendRequest(token, friendEmailId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (InvalidTokenException invalidTokenException) {
            return new ResponseEntity<>(invalidTokenException.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (FriendNotFoundException | FriendshipNotFoundException notFoundException) {
            return new ResponseEntity<>(notFoundException.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalStateException illegalStateException) {
            return new ResponseEntity<>(illegalStateException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-friend-requests")
    public ResponseEntity<List<FriendshipDto>> getAllRequests(@RequestHeader("Authorization") String token) {
        try {
            List<FriendshipDto> friendshipDtos = friendService.getAllRequests(token);
            return new ResponseEntity<>(friendshipDtos, HttpStatus.OK);
        } catch (InvalidTokenException invalidTokenException) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
        } catch (Exception ex) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-friend-list")
    public ResponseEntity<List<FriendshipDto>> getFriendList(@RequestHeader("Authorization") String token) {
        try {
            List<FriendshipDto> friendshipDtos = friendService.getAllFriends(token);
            return new ResponseEntity<>(friendshipDtos, HttpStatus.OK);
        } catch (InvalidTokenException invalidTokenException) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
        } catch (FriendNotFoundException notFoundException) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/poke/{friendEmailId}")
    public ResponseEntity<Void> pokeFriend(@RequestHeader("Authorization")String token,
                                           @PathVariable("friendEmailId") String friendEmailId) {
        try {
            friendService.pokeFriend(token, friendEmailId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (InvalidTokenException invalidTokenException) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (FriendNotFoundException friendNotFoundException) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
