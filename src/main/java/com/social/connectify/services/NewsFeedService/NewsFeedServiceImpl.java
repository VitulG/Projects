package com.social.connectify.services.NewsFeedService;

import com.social.connectify.dto.NewsFeedDto;
import com.social.connectify.dto.PostDto;
import com.social.connectify.exceptions.InvalidTokenException;
import com.social.connectify.exceptions.PostNotFoundException;
import com.social.connectify.exceptions.UserNotFoundException;
import com.social.connectify.models.Group;
import com.social.connectify.models.Post;
import com.social.connectify.models.Token;
import com.social.connectify.models.User;
import com.social.connectify.repositories.GroupRepository;
import com.social.connectify.repositories.TokenRepository;
import com.social.connectify.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class NewsFeedServiceImpl implements NewsFeedService{

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final GroupRepository groupRepository;

    @Autowired
    public NewsFeedServiceImpl(UserRepository userRepository, TokenRepository tokenRepository,
                               GroupRepository groupRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.groupRepository = groupRepository;
    }

    @Override
    public List<NewsFeedDto> getNewsFeed(String token) throws InvalidTokenException, UserNotFoundException, PostNotFoundException {
        Optional<Token> optionalToken = tokenRepository.findByToken(token);

        if (optionalToken.isEmpty()) {
            throw new InvalidTokenException("Invalid token");
        }

        Token existingToken = optionalToken.get();
        if (existingToken.isDeleted() || !existingToken.isActive() || existingToken.getExpireAt().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Token is either expired, deleted, or inactive");
        }

        User user = existingToken.getUser();
        if (user == null || user.isDeleted()) {
            throw new UserNotFoundException("User does not exist");
        }

        Set<User> userFriends = Optional.ofNullable(user.getUserFriends()).orElse(Collections.emptySet());
        Set<Group> userGroups = Optional.ofNullable(user.getUserGroups()).orElse(Collections.emptySet());

        List<NewsFeedDto> newsFeedDtos = new ArrayList<>();
        populateFriendPosts(userFriends, newsFeedDtos);
        populateGroupPosts(userGroups, newsFeedDtos);

        return newsFeedDtos;
    }

    private void populateFriendPosts(Set<User> userFriends, List<NewsFeedDto> newsFeedDtos) {
        for (User friend : userFriends) {
            for (Post post : friend.getUserPosts()) {
                NewsFeedDto newsFeedDto = createNewsFeedDto(friend.getFirstName(), friend.getLastName(), post);
                newsFeedDtos.add(newsFeedDto);
            }
        }
    }

    private void populateGroupPosts(Set<Group> userGroups, List<NewsFeedDto> newsFeedDtos) {
        for (Group group : userGroups) {
            for (Post post : group.getPosts()) {
                NewsFeedDto newsFeedDto = createNewsFeedDto(group.getGroupName(), "", post);
                newsFeedDtos.add(newsFeedDto);
            }
        }
    }

    private NewsFeedDto createNewsFeedDto(String firstName, String lastName, Post post) {
        PostDto postDto = new PostDto();
        postDto.setContent(post.getContent());
        postDto.setNumberOfComments(post.getNumberOfComments());
        postDto.setNumberOfTimesPostShared(post.getNumberOfTimesPostShared());
        postDto.setNumberOfLikes(post.getNumberOfLikes());
        postDto.setImages(post.getImages());
        postDto.setVideos(post.getVideos());

        NewsFeedDto newsFeedDto = new NewsFeedDto();
        newsFeedDto.setFirstName(firstName);
        newsFeedDto.setLastName(lastName);
        newsFeedDto.setPost(postDto);
        return newsFeedDto;
    }
}
