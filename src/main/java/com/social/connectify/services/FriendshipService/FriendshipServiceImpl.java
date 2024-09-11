package com.social.connectify.services.FriendshipService;

import com.social.connectify.dto.FriendRequestDto;
import org.springframework.stereotype.Service;

@Service
public class FriendshipServiceImpl implements FriendshipService {

    @Override
    public String respondFriendRequest(FriendRequestDto friendRequestDto, String token) {
        return "";
    }
}
