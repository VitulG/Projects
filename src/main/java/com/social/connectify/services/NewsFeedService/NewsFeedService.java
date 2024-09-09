package com.social.connectify.services.NewsFeedService;

import com.social.connectify.dto.NewsFeedDto;
import com.social.connectify.exceptions.InvalidTokenException;
import com.social.connectify.exceptions.PostNotFoundException;
import com.social.connectify.exceptions.UserNotFoundException;

import java.util.List;

public interface NewsFeedService {
    List<NewsFeedDto> getNewsFeed(String token) throws InvalidTokenException, UserNotFoundException, PostNotFoundException;
}
