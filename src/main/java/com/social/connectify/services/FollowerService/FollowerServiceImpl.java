package com.social.connectify.services.FollowerService;

import com.social.connectify.dto.FollowersDto;
import com.social.connectify.dto.FollowingDto;
import com.social.connectify.dto.MutualFollowersDto;
import com.social.connectify.exceptions.*;
import com.social.connectify.models.*;
import com.social.connectify.repositories.NotificationRepository;
import com.social.connectify.repositories.TokenRepository;
import com.social.connectify.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FollowerServiceImpl implements FollowerService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final NotificationRepository notificationRepository;

    @Autowired
    public FollowerServiceImpl(UserRepository userRepository, TokenRepository tokenRepository, NotificationRepository notificationRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional
    public String followUser(String token, String userEmail) throws InvalidTokenException, UserNotFoundException,
            UserAlreadyFollowingException {
        User user = validateAndGetUser(token);
        Optional<User> optionalFollowing = userRepository.findUserByEmail(userEmail);

        if(optionalFollowing.isEmpty()) {
            throw new UserNotFoundException("user is not available");
        }

        User following = optionalFollowing.get();

        if(user.getFollowing() == null) {
            user.setFollowing(new HashSet<>());
        }

        if(user.getFollowing().contains(following)) {
            throw new UserAlreadyFollowingException("user already following");
        }

        user.getFollowing().add(following);

        if(following.getFollowers() == null){
            following.setFollowers(new HashSet<>());
        }
        following.getFollowers().add(user);

        userRepository.save(user);
        userRepository.save(following);

        createNotification(user, following);

        return "you start following "+following.getFirstName()+" "+following.getLastName();
    }

    @Override
    @Transactional
    public void unfollowUser(String token, String userEmail) throws InvalidTokenException, UserNotFoundException, UserNotFollowingException {
        User user = validateAndGetUser(token);
        Optional<User> optionalFollowing = userRepository.findUserByEmail(userEmail);

        if(optionalFollowing.isEmpty()) {
            throw new UserNotFoundException("user is not available");
        }

        User following = optionalFollowing.get();

        if(user.getFollowing() == null || !user.getFollowing().contains(following)) {
            throw new UserNotFollowingException("you are not following this user");
        }

        user.getFollowing().remove(following);
        following.getFollowers().remove(user);

        userRepository.save(user);
        userRepository.save(following);
    }

    @Override
    @Transactional
    public List<FollowersDto> getAllFollowers(String token) throws InvalidTokenException, FollowersNotFoundException {
        User user = validateAndGetUser(token);

        Set<User> userFollowers = user.getFollowers();

        if(userFollowers.isEmpty()) {
            throw new FollowersNotFoundException("no followers found");
        }

        return userFollowers
                .stream()
                .map(userFollower -> new FollowersDto(userFollower.getFirstName(), userFollower.getLastName()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<FollowingDto> getAllFollowing(String token) throws InvalidTokenException, UserNotFollowingAnyoneException {
        User user = validateAndGetUser(token);

        // Check if the user is following anyone
        Set<User> userFollowing = user.getFollowing();
        if (userFollowing.isEmpty()) {
            throw new UserNotFollowingAnyoneException("The user is not following anyone.");
        }

        // Convert the set of following users to a list of DTOs using Stream API
        return userFollowing.stream()
                .map(followingUser -> new FollowingDto(followingUser.getFirstName(), followingUser.getLastName()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<MutualFollowersDto> getMutualFollowers(String token, String anotherUser) throws InvalidTokenException, FollowersNotFoundException, UserNotFoundException {
        User user = validateAndGetUser(token);
        Optional<User> optionalUser = userRepository.findUserByEmail(anotherUser);

        if(optionalUser.isEmpty()) {
            throw new UserNotFoundException("given user is not available");
        }
        User targetUser = optionalUser.get();
        Set<User> mutualFollowersSet = new HashSet<>(user.getFollowers());
        mutualFollowersSet.retainAll(targetUser.getFollowers());

        if(mutualFollowersSet.isEmpty()) {
            throw new FollowersNotFoundException("no mutual followers found");
        }

        return mutualFollowersSet.stream()
                .map(follower -> new MutualFollowersDto(follower.getFirstName(), follower.getLastName()))
                .toList();
    }

    @Override
    @Transactional
    public Long getUserFollowersCount(String token) throws InvalidTokenException {
        User user = validateAndGetUser(token);
        return (long) user.getFollowers().size();
    }

    @Override
    public Long getUserFollowingCount(String token) throws InvalidTokenException {
        User user = validateAndGetUser(token);
        return (long) user.getFollowing().size();
    }

    private User validateAndGetUser(String token) throws InvalidTokenException {
        Optional<Token> optionalToken = tokenRepository.findByToken(token);

        if(optionalToken.isEmpty()) {
            throw new InvalidTokenException("invalid token");
        }

        Token existingToken = optionalToken.get();

        if(!existingToken.isActive() || existingToken.getExpireAt().isBefore(LocalDateTime.now()) || existingToken.isDeleted()) {
            throw new InvalidTokenException("token is not active or expired");
        }
        return existingToken.getUser();
    }

    private void createNotification(User user, User following) {
        Notification notification = new Notification();
        notification.setUser(following);
        notification.setStatus(NotificationStatus.UNREAD);
        notification.setMessage(user.getFirstName()+" "+user.getLastName()+" started following you");
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }
}
