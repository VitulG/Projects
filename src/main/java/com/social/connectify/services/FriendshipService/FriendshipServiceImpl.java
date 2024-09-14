package com.social.connectify.services.FriendshipService;

import com.social.connectify.dto.FriendRequestDto;
import com.social.connectify.dto.FriendshipDto;
import com.social.connectify.exceptions.FriendNotFoundException;
import com.social.connectify.exceptions.FriendshipNotFoundException;
import com.social.connectify.exceptions.InvalidTokenException;
import com.social.connectify.exceptions.UserNotFoundException;
import com.social.connectify.models.*;
import com.social.connectify.repositories.FriendshipRepository;
import com.social.connectify.repositories.NotificationRepository;
import com.social.connectify.repositories.TokenRepository;
import com.social.connectify.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class FriendshipServiceImpl implements FriendshipService {
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final NotificationRepository notificationRepository;

    @Autowired
    public FriendshipServiceImpl(TokenRepository tokenRepository, UserRepository userRepository,
                                 FriendshipRepository friendshipRepository, NotificationRepository notificationRepository) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional
    public String respondFriendRequest(FriendRequestDto friendRequestDto, String token) throws InvalidTokenException,
            FriendshipNotFoundException, FriendNotFoundException {

        User user = validateTokenAndGetUser(token);
        User friend = findFriend(friendRequestDto.getEmail());

        Optional<Friendship> friendshipOptional = friendshipRepository.findByUserAndFriend(friend, user);

        if(friendshipOptional.isEmpty()) {
            throw new FriendshipNotFoundException("friend request does not exist");
        }

        Friendship friendship = friendshipOptional.get();

        if(friendship.getStatus() != FriendRequestStatus.PENDING) {
            throw new IllegalStateException("friend request has already been responded to");
        }

        if(friendRequestDto.getRequestAction().equalsIgnoreCase(FriendRequestStatus.REJECTED.toString())) {
            friendship.setStatus(FriendRequestStatus.REJECTED);
            friendshipRepository.save(friendship);
            return "friend request rejected";
        }
        friendship.setStatus(FriendRequestStatus.ACCEPTED);
        friendship.setRequestAcceptedDate(LocalDateTime.now());
        friendshipRepository.save(friendship);

        if(user.getFriendshipsReceived() == null) {
            user.setFriendshipsReceived(new HashSet<>());
        }
        user.getFriendshipsReceived().add(friendship);
        if(user.getUserFriends() == null) {
            user.setUserFriends(new HashSet<>());
        }
        user.getUserFriends().add(friend);

        if (friend.getUserFriends() == null) {
            friend.setUserFriends(new HashSet<>());
        }
        friend.getUserFriends().add(user);

        Notification notification = new Notification();
        notification.setStatus(NotificationStatus.UNREAD);
        notification.setUser(friend);
        notification.setMessage(user.getFirstName()+" "+user.getLastName()+" has accepted your friend request");
        notification.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notification);

        return "friend request accepted";
    }

    @Override
    @Transactional
    public String addAFriend(String token, String friendEmailId) throws InvalidTokenException, FriendNotFoundException {
        User user = validateTokenAndGetUser(token);
        User friend = findFriend(friendEmailId);

        if(user.getEmail().equals(friend.getEmail())) {
            throw new IllegalArgumentException("You cannot add yourself as a friend");
        }

        Friendship friendship = new Friendship();
        friendship.setFriend(friend);
        friendship.setUser(user);
        friendship.setStatus(FriendRequestStatus.PENDING);
        friendship.setRequestSentDate(LocalDateTime.now());
        friendship.setCreatedAt(LocalDateTime.now());

        if(user.getFriendshipsSent() == null) {
            user.setFriendshipsSent(new HashSet<>());
        }
        user.getFriendshipsSent().add(friendship);

        Notification notification = new Notification();
        notification.setCreatedAt(LocalDateTime.now());
        notification.setStatus(NotificationStatus.UNREAD);
        notification.setUser(friend);
        notification.setMessage(user.getFirstName()+" "+user.getLastName()+" has sent you a friend request");
        notificationRepository.save(notification);

        return "friend request sent";
    }

    @Override
    @Transactional
    public String cancelFriendRequest(String token, String friendEmailId) throws InvalidTokenException, FriendNotFoundException, FriendshipNotFoundException {
        User user = validateTokenAndGetUser(token);
        User friend = findFriend(friendEmailId);

        Optional<Friendship> friendshipOptional = friendshipRepository.findByUserAndFriend(friend, user);

        if(friendshipOptional.isEmpty()) {
            throw new FriendshipNotFoundException("friend request does not exist");
        }

        Friendship currentFriendship =  friendshipOptional.get();

        if (currentFriendship.getStatus() != FriendRequestStatus.PENDING) {
            throw new IllegalStateException("Only pending requests can be cancelled.");
        }

        currentFriendship.setStatus(FriendRequestStatus.CANCELLED);

        friendshipRepository.save(currentFriendship);

        return "friend request cancelled";
    }

    @Override
    @Transactional
    public List<FriendshipDto> getAllRequests(String token) throws InvalidTokenException, FriendshipNotFoundException {
        User user = validateTokenAndGetUser(token);
        List<Friendship> friendships = friendshipRepository.findAllFriendshipByStatus(FriendRequestStatus.PENDING);

        if(friendships == null || friendships.isEmpty()) {
            throw new FriendshipNotFoundException("no friend requests to respond");
        }

        List<FriendshipDto> friendRequests = new ArrayList<>();

        for(Friendship friendship : friendships) {
            FriendshipDto friendRequestDto = new FriendshipDto();
            friendRequests.add(friendship.convertToFriendshipDto());
        }
        return friendRequests;
    }

    @Override
    @Transactional
    public List<FriendshipDto> getAllFriends(String token) throws InvalidTokenException, FriendNotFoundException {
        User user = validateTokenAndGetUser(token);

        Set<User> friends = user.getUserFriends();

        if(friends == null || friends.isEmpty()) {
            throw new FriendNotFoundException("no friends found");
        }

        List<FriendshipDto> friendsList = new ArrayList<FriendshipDto>();

        for (User friend : friends) {
            FriendshipDto friendDto = new FriendshipDto();
            friendDto.setFriendFirstName(friend.getFirstName());
            friendDto.setFriendLastName(friend.getLastName());
            friendsList.add(friendDto);
        }
        return friendsList;
    }

    @Override
    @Transactional
    public void pokeFriend(String token, String friendEmailId) throws InvalidTokenException, FriendNotFoundException {
        User user = validateTokenAndGetUser(token);
        User friend = findFriend(friendEmailId);

        if(!user.getUserFriends().contains(friend)) {
            throw new FriendNotFoundException("not in your friend list");
        }

        // create a new notification for the friend that he has poked you
        Notification notification = new Notification();
        notification.setStatus(NotificationStatus.UNREAD);
        notification.setUser(friend);
        notification.setMessage(user.getFirstName()+" "+user.getLastName()+" poked you!");
        notification.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }

    private User validateTokenAndGetUser(String token) throws InvalidTokenException {
        Optional<Token> optionalToken = tokenRepository.findByToken(token);

        if(optionalToken.isEmpty()) {
            throw new InvalidTokenException("token not found");
        }
        Token existingToken = optionalToken.get();
        if(existingToken.isDeleted() || !existingToken.isActive() ||
                existingToken.getExpireAt().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("either the token is not active or expired");
        }
        return existingToken.getUser();
    }

    private User findFriend(String friendEmailId) throws FriendNotFoundException {
        Optional<User> optionalFriend = userRepository.findUserByEmail(friendEmailId);

        if(optionalFriend.isEmpty()) {
            throw new FriendNotFoundException("user is not available");
        }
        return optionalFriend.get();
    }
}
