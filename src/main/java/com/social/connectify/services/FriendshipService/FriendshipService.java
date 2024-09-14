package com.social.connectify.services.FriendshipService;

import com.social.connectify.dto.FriendRequestDto;
import com.social.connectify.dto.FriendshipDto;
import com.social.connectify.exceptions.FriendNotFoundException;
import com.social.connectify.exceptions.FriendshipNotFoundException;
import com.social.connectify.exceptions.InvalidTokenException;
import com.social.connectify.exceptions.UserNotFoundException;

import java.util.List;

public interface FriendshipService {
    public String respondFriendRequest(FriendRequestDto friendRequestDto, String token) throws InvalidTokenException, UserNotFoundException, FriendshipNotFoundException, FriendNotFoundException;
    public String addAFriend(String token, String friendEmailId) throws InvalidTokenException, FriendNotFoundException;
    public String cancelFriendRequest(String token, String friendEmailId) throws InvalidTokenException, FriendNotFoundException, FriendshipNotFoundException;
    public List<FriendshipDto> getAllRequests(String token) throws InvalidTokenException, FriendshipNotFoundException;
    public List<FriendshipDto> getAllFriends(String token) throws InvalidTokenException, FriendNotFoundException;
    public void pokeFriend(String token, String friendEmailId) throws InvalidTokenException, FriendNotFoundException;
}
