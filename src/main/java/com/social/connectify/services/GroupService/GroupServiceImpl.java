package com.social.connectify.services.GroupService;

import com.social.connectify.dto.GroupCreationDto;
import com.social.connectify.dto.GroupMembersDto;
import com.social.connectify.dto.RespondGroupRequestDto;
import com.social.connectify.exceptions.*;
import com.social.connectify.models.*;
import com.social.connectify.repositories.*;
import com.social.connectify.validations.GroupCreationValidator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GroupServiceImpl implements GroupService {
    private final TokenRepository tokenRepository;
    private final ImageRepository imageRepository;
    private final GroupMembershipRepository groupMembershipRepository;
    private final GroupRepository groupRepository;
    private final GroupCreationValidator groupCreationValidator;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    @Autowired
    public GroupServiceImpl(TokenRepository tokenRepository, ImageRepository imageRepository,
                            GroupMembershipRepository groupMembershipRepository, GroupRepository groupRepository,
                            GroupCreationValidator groupCreationValidator, UserRepository userRepository,
                            NotificationRepository notificationRepository) {
        this.tokenRepository = tokenRepository;
        this.imageRepository = imageRepository;
        this.groupMembershipRepository = groupMembershipRepository;
        this.groupRepository = groupRepository;
        this.groupCreationValidator = groupCreationValidator;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional
    public String createGroup(String token, GroupCreationDto groupCreationDto) throws InvalidTokenException, GroupCreationException {
        User user = validateAndGetUser(token);

        groupCreationValidator.validateGroupCreationDetails(groupCreationDto.getGroupName(), groupCreationDto.getGroupDescription());

        Group group = new Group();
        group.setGroupName(groupCreationDto.getGroupName());
        group.setGroupDescription(groupCreationDto.getGroupDescription());

        if(groupCreationDto.getGroupImage() != null || !groupCreationDto.getGroupImage().isEmpty()) {
            Image groupImage = new Image();
            groupImage.setImageUrl(groupCreationDto.getGroupImage());
            groupImage.setCreatedAt(LocalDateTime.now());
            groupImage.setGroup(group);
            imageRepository.save(groupImage);
            group.setGroupImage(groupImage);
        }

        group.setCreatedAt(LocalDateTime.now());

        if(group.getGroupMemberships() == null) {
            group.setGroupMemberships(new ArrayList<>());
        }
        GroupMembership groupMembership = new GroupMembership();
        groupMembership.setGroup(group);
        groupMembership.setUser(user);
        groupMembership.setRole(GroupUserRole.ADMIN);
        groupMembership.setStatus(JoinGroupStatus.ACCEPTED);
        groupMembershipRepository.save(groupMembership);
        group.getGroupMemberships().add(groupMembership);

        if(user.getGroupMemberships() == null) {
            user.setGroupMemberships(new ArrayList<>());
        }
        user.getGroupMemberships().add(groupMembership);

        if(user.getUserGroups() == null) {
            user.setUserGroups(new HashSet<>());
        }
        user.getUserGroups().add(group);
        userRepository.save(user);

        if(group.getUsers() == null) {
            group.setUsers(new HashSet<>());
        }
        group.getUsers().add(user);

        groupRepository.save(group);
        return "group created with id: "+group.getGroupId();
    }

    @Override
    @Transactional
    public String joinGroup(String token, Long groupId) throws InvalidTokenException, GroupNotFoundException {
        User user = validateAndGetUser(token);
        Optional<Group> optionalGroup = groupRepository.findByGroupId(groupId);

        if(optionalGroup.isEmpty()) {
            throw new GroupNotFoundException("Group not found with the given id.");
        }
        Group group = optionalGroup.get();

        // Check if the user has already sent a join request
        for (GroupMembership membership : group.getGroupMemberships()) {
            if (membership.getUser().getUserId().equals(user.getUserId()) &&
                    membership.getStatus() == JoinGroupStatus.PENDING) {
                return "You have already requested to join this group.";
            }
        }

        List<User> groupAdmins = new ArrayList<>();

        for(GroupMembership member : group.getGroupMemberships()) {
            if(member.getRole() == GroupUserRole.ADMIN) {
                groupAdmins.add(member.getUser());
            }
        }
        // notify the group admins
        createNotificationForAdmin(user, groupAdmins, group);

        GroupMembership membership = new GroupMembership();
        membership.setStatus(JoinGroupStatus.PENDING);
        membership.setGroup(group);
        membership.setUser(user);
        membership.setRole(GroupUserRole.USER);
        membership.setCreatedAt(LocalDateTime.now());
        groupMembershipRepository.save(membership);

        return "request send to the group with membership id: "+membership.getGroupMembershipId();
    }

    @Override
    @Transactional
    public String respondRequest(String token, RespondGroupRequestDto respondGroupRequestDto) throws InvalidTokenException, GroupMembershipNotFoundException, UnauthorizedUserException {
        User admin = validateAndGetUser(token);
        GroupMembership membership = groupMembershipRepository
                .findById(respondGroupRequestDto.getMembershipId())
                .orElseThrow(() -> new GroupMembershipNotFoundException("membership not found"));

        boolean isAdmin = membership.getGroup().getGroupMemberships().stream()
                .anyMatch(m -> m.getUser().getUserId().equals(admin.getUserId()) &&
                        m.getRole() == GroupUserRole.ADMIN);

        if(!isAdmin) {
            throw new UnauthorizedUserException("Only group admins can respond to join requests.");
        }

        Group group = membership.getGroup();
        if(respondGroupRequestDto.getResponse().equalsIgnoreCase(JoinGroupStatus.ACCEPTED.toString())) {
            membership.setStatus(JoinGroupStatus.ACCEPTED);
            group.getGroupMemberships().add(membership);
            group.getUsers().add(membership.getUser());
            if(membership.getUser().getGroupMemberships() == null) {
                membership.getUser().setGroupMemberships(new ArrayList<>());
            }
            membership.getUser().getGroupMemberships().add(membership);
            groupRepository.save(group);
            groupMembershipRepository.save(membership);

            createNotificationForUser(admin, membership.getUser(), group);

            return "you added "+membership.getUser().getFirstName()+" "+membership.getUser().getLastName() +
                    " to the group";
        }else if(respondGroupRequestDto.getResponse().equalsIgnoreCase(JoinGroupStatus.REJECTED.toString())) {
            membership.setStatus(JoinGroupStatus.REJECTED);
            groupMembershipRepository.save(membership);
            return "rejected";
        }else {
            throw new IllegalArgumentException("Invalid response");
        }
    }

    @Override
    @Transactional
    public List<GroupMembersDto> getAllGroupMembers(String token, Long groupId) throws InvalidTokenException, GroupNotFoundException, UnauthorizedUserException {
        User user = validateAndGetUser(token);

        Optional<Group> optionalGroup = groupRepository.findByGroupId(groupId);
        if(optionalGroup.isEmpty()) {
            throw new GroupNotFoundException("Group not found with the given id.");
        }
        Group group = optionalGroup.get();

        boolean isGroupMember = group.getGroupMemberships().stream()
                .anyMatch(membership -> membership.getUser().equals(user));

        if(!isGroupMember) {
            throw new UnauthorizedUserException("you are not authorized to view this group");
        }

        return group.getGroupMemberships().stream()
                .map(membership -> {
                    GroupMembersDto groupMembersDto = new GroupMembersDto();
                    groupMembersDto.setUserName(membership.getUser().getFirstName() + " " + membership.getUser().getLastName());
                    groupMembersDto.setRole(membership.getRole().toString());
                    return groupMembersDto;
                })
                .toList();
    }

    @Override
    @Transactional
    public String makeUserAdmin(String token, Long groupId, Long userId) throws InvalidTokenException, UserNotFoundException, GroupNotFoundException, UnauthorizedUserException {
        User admin = validateAndGetUser(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with the given id."));

        Group group = groupRepository.findByGroupId(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group not found with the given id."));


        boolean isGroupAdmin = group.getGroupMemberships().stream()
                .anyMatch(membership -> membership.getUser().equals(admin) &&
                        membership.getRole() == GroupUserRole.ADMIN);

        if(!isGroupAdmin) {
            throw new UnauthorizedUserException("Only group admins can make other users admins.");
        }

        GroupMembership targetMembership = group.getGroupMemberships().stream()
                .filter(membership -> membership.getUser().equals(user))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("User is not part of this group."));

        targetMembership.setRole(GroupUserRole.ADMIN);
        groupMembershipRepository.save(targetMembership);

       return "Ok";
    }

    private void createNotificationForUserPromotion(User admin, User user, Group group) {
        Notification notification = new Notification();
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUser(user);
        notification.setMessage(admin.getFirstName()+" "+admin.getLastName()+" has made you an admin of the group "+
                group.getGroupName());
        notification.setStatus(NotificationStatus.UNREAD);
        notificationRepository.save(notification);
    }

    private void createNotificationForUser(User admin, User requested, Group group) {
        Notification notification = new Notification();
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUser(requested);
        notification.setMessage(admin.getFirstName()+" "+admin.getLastName()+" has accepted your request");
        notification.setStatus(NotificationStatus.UNREAD);
        notificationRepository.save(notification);
    }

    private void createNotificationForAdmin(User user, List<User> admins, Group group) {
        for(User admin : admins) {
            Notification notification = new Notification();
            notification.setCreatedAt(LocalDateTime.now());
            notification.setMessage(user.getFirstName()+" "+user.getLastName() +" wants to join the group "+group.getGroupName());
            notification.setUser(admin);
            notification.setStatus(NotificationStatus.UNREAD);
            notificationRepository.save(notification);
        }
    }

    private User validateAndGetUser(String token) throws InvalidTokenException {
        Optional<Token> optionalToken = tokenRepository.findByToken(token);
        if(optionalToken.isEmpty()) {
            throw new InvalidTokenException("Token not found");
        }
        Token existingToken = optionalToken.get();

        if(!existingToken.isActive() || existingToken.getExpireAt().isBefore(java.time.LocalDateTime.now()) || existingToken.isDeleted()) {
            throw new InvalidTokenException("Either the token is not active or expired");
        }
        return existingToken.getUser();
    }
}
