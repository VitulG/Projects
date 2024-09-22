package com.social.connectify.services.FollowerService;

import com.social.connectify.dto.FollowersDto;
import com.social.connectify.dto.FollowingDto;
import com.social.connectify.dto.MutualFollowersDto;
import com.social.connectify.exceptions.*;

import java.util.List;

public interface FollowerService {
    String followUser(String token, String userEmail) throws InvalidTokenException, UserNotFoundException, UserAlreadyFollowingException;
    void unfollowUser(String token, String userEmail) throws InvalidTokenException, UserNotFoundException, UserNotFollowingException;
    List<FollowersDto> getAllFollowers(String token) throws InvalidTokenException, FollowersNotFoundException;
    List<FollowingDto> getAllFollowing(String token) throws InvalidTokenException, UserNotFollowingAnyoneException;
    List<MutualFollowersDto> getMutualFollowers(String token, String anotherUser) throws InvalidTokenException, FollowersNotFoundException, UserNotFoundException;
    Long getUserFollowersCount(String token) throws InvalidTokenException;
    Long getUserFollowingCount(String token) throws InvalidTokenException;
}
