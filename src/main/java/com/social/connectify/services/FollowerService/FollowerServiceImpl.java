package com.social.connectify.services.FollowerService;

import com.social.connectify.exceptions.InvalidTokenException;
import com.social.connectify.exceptions.UserAlreadyFollowingException;
import com.social.connectify.exceptions.UserNotFollowingException;
import com.social.connectify.exceptions.UserNotFoundException;
import com.social.connectify.models.*;
import com.social.connectify.repositories.NotificationRepository;
import com.social.connectify.repositories.TokenRepository;
import com.social.connectify.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

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
