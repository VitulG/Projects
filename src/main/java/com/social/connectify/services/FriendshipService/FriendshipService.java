package com.social.connectify.services.FriendshipService;

import com.social.connectify.dto.FriendRequestDto;

public interface FriendshipService {
    public String respondFriendRequest(FriendRequestDto friendRequestDto, String token);
}
