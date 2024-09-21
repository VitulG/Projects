package com.social.connectify.controllers;

import com.social.connectify.exceptions.InvalidTokenException;
import com.social.connectify.exceptions.UserAlreadyFollowingException;
import com.social.connectify.exceptions.UserNotFollowingException;
import com.social.connectify.exceptions.UserNotFoundException;
import com.social.connectify.services.FollowerService.FollowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/follow")
public class FollowerController {
    private final FollowerService followerService;

    @Autowired
    public FollowerController(FollowerService followerService) {
        this.followerService = followerService;
    }

    @PostMapping("/{userEmail}/follow-user")
    public ResponseEntity<String> followUser(@RequestHeader("Authorization") String token,
                                             @PathVariable("userEmail") String userEmail) {
        // logic goes here
        try {
            String response = followerService.followUser(token, userEmail);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (InvalidTokenException invalidTokenException) {
            return new ResponseEntity<>(invalidTokenException.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (UserAlreadyFollowingException userAlreadyFollowingException) {
            return new ResponseEntity<>(userAlreadyFollowingException.getMessage(), HttpStatus.CONFLICT);
        } catch (UserNotFoundException userNotFoundException) {
            return new ResponseEntity<>(userNotFoundException.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{userEmail}/unfollow-user")
    public ResponseEntity<Void> unfollowUser(@RequestHeader("Authorization") String token,
                                               @PathVariable("userEmail") String userEmail) {
        try {
            followerService.unfollowUser(token, userEmail);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (InvalidTokenException invalidTokenException) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (UserNotFoundException | UserNotFollowingException userNotFoundException) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch(Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
