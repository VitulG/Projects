package com.social.connectify.controllers;

import com.social.connectify.dto.FollowersDto;
import com.social.connectify.dto.FollowingDto;
import com.social.connectify.dto.MutualFollowersDto;
import com.social.connectify.exceptions.*;
import com.social.connectify.services.FollowerService.FollowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user/followers")
    public ResponseEntity<List<FollowersDto>> getAllFollowers(@RequestHeader("Authorization") String token) {
        try {
            List<FollowersDto> followersDtos = followerService.getAllFollowers(token);
            return new ResponseEntity<>(followersDtos, HttpStatus.OK);
        } catch (InvalidTokenException invalidTokenException) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
        } catch (FollowersNotFoundException followersNotFoundException) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user/following")
    public ResponseEntity<List<FollowingDto>> getAllFollowings(@RequestHeader("Authorization") String token) {
        try {
            List<FollowingDto> followingDto = followerService.getAllFollowing(token);
            return new ResponseEntity<>(followingDto, HttpStatus.OK);
        } catch (InvalidTokenException invalidTokenException) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
        } catch (UserNotFollowingAnyoneException userNotFollowingAnyoneException) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{userEmailId}/users/mutual-followers")
    public ResponseEntity<List<MutualFollowersDto>> getMutualFollowers(@RequestHeader("Authorization") String token,
                                                                       @PathVariable("userEmailId") String userEmailId) {
        try {
            List<MutualFollowersDto> mutualFollowersDtos = followerService.getMutualFollowers(token, userEmailId);
            return new ResponseEntity<>(mutualFollowersDtos, HttpStatus.OK);
        } catch(InvalidTokenException invalidTokenException) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
        }catch (UserNotFoundException | FollowersNotFoundException notFoundException) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/followers/count")
    public ResponseEntity<Long> getTotalFollowers(@RequestHeader("Authorization") String token) {
        try {
            Long totalFollowers = followerService.getUserFollowersCount(token);
            return new ResponseEntity<>(totalFollowers, HttpStatus.OK);
        } catch (InvalidTokenException invalidTokenException) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/followings/count")
    public ResponseEntity<Long> getTotalFollowings(@RequestHeader("Authorization") String token) {
        try {
            Long totalFollowings = followerService.getUserFollowingCount(token);
            return new ResponseEntity<>(totalFollowings, HttpStatus.OK);
        } catch (InvalidTokenException invalidTokenException) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
