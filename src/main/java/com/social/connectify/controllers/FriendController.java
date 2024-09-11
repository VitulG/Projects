package com.social.connectify.controllers;

import com.social.connectify.dto.FriendRequestDto;
import com.social.connectify.services.FriendshipService.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        // logic
        try {
            String response = friendService.respondFriendRequest(friendRequestDto, token);
        }

    }
}
