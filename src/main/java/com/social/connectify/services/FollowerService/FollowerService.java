package com.social.connectify.services.FollowerService;

import com.social.connectify.exceptions.InvalidTokenException;
import com.social.connectify.exceptions.UserAlreadyFollowingException;
import com.social.connectify.exceptions.UserNotFollowingException;
import com.social.connectify.exceptions.UserNotFoundException;

public interface FollowerService {
    String followUser(String token, String userEmail) throws InvalidTokenException, UserNotFoundException, UserAlreadyFollowingException;
    void unfollowUser(String token, String userEmail) throws InvalidTokenException, UserNotFoundException, UserNotFollowingException;
}
