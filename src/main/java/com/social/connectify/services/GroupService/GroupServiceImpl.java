package com.social.connectify.services.GroupService;

import com.social.connectify.dto.*;
import com.social.connectify.enums.*;
import com.social.connectify.exceptions.*;
import com.social.connectify.models.*;
import com.social.connectify.repositories.*;
import com.social.connectify.validations.GroupCreationValidator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
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
    private final VideoRepository videoRepository;
    private final MessageRepository messageRepository;

    @Autowired
    public GroupServiceImpl(TokenRepository tokenRepository, ImageRepository imageRepository,
                            GroupMembershipRepository groupMembershipRepository, GroupRepository groupRepository,
                            GroupCreationValidator groupCreationValidator, UserRepository userRepository,
                            NotificationRepository notificationRepository, VideoRepository videoRepository, MessageRepository messageRepository) {
        this.tokenRepository = tokenRepository;
        this.groupMembershipRepository = groupMembershipRepository;
        this.groupRepository = groupRepository;
        this.groupCreationValidator = groupCreationValidator;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
        this.imageRepository = imageRepository;
        this.videoRepository = videoRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    @Transactional
    public String createGroup(String token, GroupCreationDto groupCreationDto) throws InvalidTokenException, GroupCreationException {
        User user = validateAndGetUser(token);

        groupCreationValidator.validateGroupCreationDetails(groupCreationDto.getGroupName(), groupCreationDto.getGroupDescription());

        Group group = new Group();
        group.setGroupName(groupCreationDto.getGroupName());
        group.setGroupDescription(groupCreationDto.getGroupDescription());
        group.setCreatedAt(LocalDateTime.now());

        if(groupCreationDto.getGroupImage() != null && !groupCreationDto.getGroupImage().isEmpty()) {
            Image groupImage = new Image();
            groupImage.setImageUrl(groupCreationDto.getGroupImage());
            groupImage.setCreatedAt(LocalDateTime.now());
            groupImage.setGroup(group);
            imageRepository.save(groupImage);
            group.setGroupImage(groupImage);
        }

        if(group.getGroupMemberships() == null) {
            group.setGroupMemberships(new ArrayList<>());
        }

        GroupMembership groupMembership = new GroupMembership();
        groupMembership.setCreatedAt(LocalDateTime.now());
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

        group.setGroupStatus(GroupStatus.ACTIVE);

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
        createNotificationForAdmin(user, groupAdmins, group, GroupNotificationType.JOIN_REQUEST_APPROVAL);

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

        if(membership.getStatus() == JoinGroupStatus.ACCEPTED) {
            throw new GroupMembershipNotFoundException("this membership is already accepted");
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

            User user = membership.getUser();
            if(user.getUserGroups() == null) {
                user.setUserGroups(new HashSet<>());
            }
            user.getUserGroups().add(group);

            userRepository.save(user);
            groupRepository.save(group);
            groupMembershipRepository.save(membership);

            createNotificationForUser(admin, membership.getUser(), group, GroupNotificationType.JOIN_REQUEST_APPROVED);

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
                .filter(membership -> membership.getStatus() != JoinGroupStatus.LEFT
                && membership.getStatus()!= JoinGroupStatus.REJECTED)
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
        createNotificationForUser(admin, user, group, GroupNotificationType.PROMOTION);
        groupMembershipRepository.save(targetMembership);

       return "Ok";
    }

    @Override
    @Transactional
    public String leaveGroup(String token, Long groupId) throws InvalidTokenException, GroupNotFoundException, UnauthorizedUserException {
        User user = validateAndGetUser(token);
        Group group = groupRepository.findByGroupId(groupId)
                .orElseThrow(() -> new GroupNotFoundException("group not found"));

        if(!group.getUsers().contains(user)) {
            throw new UnauthorizedUserException("user is not part of this group");
        }

        GroupMembership membership = group.getGroupMemberships().stream()
                .filter(m -> m.getUser().equals(user))
                .findFirst()
                .orElseThrow(() -> new UnauthorizedUserException("membership not found"));

        membership.setStatus(JoinGroupStatus.LEFT);
        membership.setDeleted(true);
        membership.setUpdatedAt(LocalDateTime.now());
        groupMembershipRepository.save(membership);

        // remove the user from the group
        group.getUsers().remove(user);
        group.getGroupMemberships().removeIf(userMembership -> userMembership.getUser().equals(user));

        user.getGroupMemberships().removeIf(userMembership -> userMembership.getGroup().getGroupId()
                .equals(group.getGroupId()));

        userRepository.save(user);
        groupRepository.save(group);

        List<User> admins = group.getGroupMemberships().stream()
                .filter(adminMembership -> adminMembership.getRole() == GroupUserRole.ADMIN)
                .map(GroupMembership::getUser)
                .toList();

        createNotificationForAdmin(user, admins, group, GroupNotificationType.USER_LEFT_GROUP);
        return "you left the group";
    }

    @Override
    @Transactional
    public String removeAMember(String token, Long groupId, Long userId) throws InvalidTokenException, GroupNotFoundException, UserNotFoundException, UnauthorizedUserException {
        User admin = validateAndGetUser(token);

        Group group = groupRepository.findByGroupId(groupId)
                .orElseThrow(() -> new GroupNotFoundException("group not found"));

        User targetedUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("user not found"));

        boolean isGroupAdmin = group.getGroupMemberships()
                .stream().anyMatch(member -> member.getUser().equals(admin)
                        && member.getRole() == GroupUserRole.ADMIN);

        if(!isGroupAdmin) {
            throw new UnauthorizedUserException("Only group admins can remove other users.");
        }

        GroupMembership membership = group.getGroupMemberships()
                .stream().filter(member -> member.getUser().equals(targetedUser))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("user does not exists in the group"));

        membership.setUpdatedAt(LocalDateTime.now());
        membership.setDeleted(true);
        membership.setStatus(JoinGroupStatus.REMOVED);
        groupMembershipRepository.save(membership);

        // remove from the group
        group.getUsers().remove(targetedUser);

        // remove user membership


        createNotificationForUser(admin, targetedUser, group, GroupNotificationType.ADMIN_REMOVED_USER);

        return "you removed "+targetedUser.getFirstName()+" "+targetedUser.getLastName()+" from the group";
    }

    @Override
    @Transactional
    public String addMembers(String token, AddMembersDto addMembersDto) throws InvalidTokenException, GroupNotFoundException, UnauthorizedUserException {
        User member = validateAndGetUser(token);

        Group group = groupRepository.findByGroupId(addMembersDto.getGroupId())
                .orElseThrow(() -> new GroupNotFoundException("group does not exist"));

        boolean isGroupMember = group.getGroupMemberships()
                .stream().anyMatch(groupMember -> groupMember.getUser().equals(member));

        if(!isGroupMember) {
            throw new UnauthorizedUserException("Only group members can add new members.");
        }

        List<User> newUsers = userRepository.findAllById(addMembersDto.getUserIds());

        List<StringBuilder> presentUsers = new ArrayList<StringBuilder>();

        for(User newUser : newUsers) {
            if(group.getUsers().contains(newUser)) {
                StringBuilder presentUser = new StringBuilder();
                presentUser.append(newUser.getFirstName()).append(" ").append(newUser.getLastName());
                presentUsers.add(presentUser);
                continue;
            }

            GroupMembership membership = new GroupMembership();
            membership.setCreatedAt(LocalDateTime.now());
            membership.setGroup(group);
            membership.setUser(newUser);
            membership.setRole(GroupUserRole.USER);
            membership.setStatus(JoinGroupStatus.ACCEPTED);

            group.getGroupMemberships().add(membership);
            group.getUsers().add(newUser);

            newUser.getGroupMemberships().add(membership);
            newUser.getUserGroups().add(group);

            createNotificationForUser(member, newUser, group, GroupNotificationType.GROUP_MEMBER_ADD_USERS);

            groupMembershipRepository.save(membership);
            userRepository.save(newUser);
        }
        groupRepository.save(group);

        if(!presentUsers.isEmpty()) {
            StringBuilder message = new StringBuilder("The following users are already in the group: \n");
            for(StringBuilder presentUser : presentUsers) {
                message.append(presentUser).append("\n");
            }
            message.append("The rest were added successfully.");
            return message.toString();
        }
        return "added successfully";
    }

    @Override
    @Transactional
    public String updateGroupDetails(String token, UpdateGroupDetailsDto detailsDto) throws InvalidTokenException, GroupNotFoundException, UnauthorizedUserException {
        User user = validateAndGetUser(token);

        Group group = groupRepository.findByGroupId(detailsDto.getGroupId())
                .orElseThrow(() -> new GroupNotFoundException("group does not exist"));

        boolean isGroupAdmin = group.getGroupMemberships()
                .stream()
                .anyMatch(member -> member.getUser().equals(user) && member.getRole() == GroupUserRole.ADMIN);

        if(!isGroupAdmin) {
            throw new UnauthorizedUserException("you are not allowed to update the group details");
        }

        if(detailsDto.getNewGroupName() != null && !detailsDto.getNewGroupName().isEmpty()) {
            group.setGroupName(detailsDto.getNewGroupName());
        }

        if(detailsDto.getNewGroupDescription() != null && !detailsDto.getNewGroupDescription().isEmpty()) {
            group.setGroupDescription(detailsDto.getNewGroupDescription());
        }

        group.setUpdatedAt(LocalDateTime.now());

        if(detailsDto.getNewImageUrl() != null && !detailsDto.getNewImageUrl().isEmpty()) {
            Image groupImage = group.getGroupImage();
            if (groupImage == null) {
                groupImage = new Image();
                groupImage.setCreatedAt(LocalDateTime.now());
                group.setGroupImage(groupImage);
            }
            groupImage.setImageUrl(detailsDto.getNewImageUrl());
            groupImage.setUpdatedAt(LocalDateTime.now());
            imageRepository.save(groupImage);
        }
        groupRepository.save(group);

        return "group details updated successfully";
    }

    @Override
    @Transactional
    public String deleteGroup(String token, Long groupId) throws InvalidTokenException, GroupNotFoundException, UnauthorizedUserException {
        User admin = validateAndGetUser(token);
        Group group = groupRepository.findByGroupId(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group does not exist"));

        boolean isGroupAdmin = group.getGroupMemberships()
                .stream()
                .anyMatch(member -> member.getUser().equals(admin) && member.getRole() == GroupUserRole.ADMIN);

        if(!isGroupAdmin) {
            throw new UnauthorizedUserException("Only group admins can delete a group");
        }

        // I will notify the users, and then I will delete the group
        for(User member : group.getUsers()) {
            if(member == admin) {
                continue;
            }
            createNotificationForUser(admin, member, group, GroupNotificationType.GROUP_DELETED);
        }

        group.setGroupStatus(GroupStatus.DELETED);
        group.setUpdatedAt(LocalDateTime.now());
        group.setDeleted(true);

        groupRepository.save(group);

        return "you deleted the group";

    }

    @Override
    @Transactional
    public String sendGroupMessage(String token, SendMessageInGroupDto sendMessageInGroupDto) throws InvalidTokenException, GroupNotFoundException, UnauthorizedUserException {
        User user = validateAndGetUser(token);

        Group group = groupRepository.findByGroupId(sendMessageInGroupDto.getGroupId())
                .orElseThrow(() -> new GroupNotFoundException("Group does not exist"));

        boolean isGroupMember = group.getGroupMemberships().stream()
                .anyMatch(member -> member.getUser().equals(user));

        if(!isGroupMember) {
            throw new UnauthorizedUserException("Only group members can send messages");
        }

        Message message = new Message();
        MediaType mediaType = getMessageMediaType(sendMessageInGroupDto);
        message.setMessageStatus(MessageStatus.SENT);
        message.setSender(user);

        if(message.getGroups() == null) {
            message.setGroups(new ArrayList<>());
        }
        message.getGroups().add(group);

        Image image = createImage(sendMessageInGroupDto, group, message);
        Video video = createVideo(sendMessageInGroupDto, message);

        if(message.getImages() == null) {
            message.setImages(new ArrayList<>());
        }
        if(image != null) {
            message.getImages().add(image);
        }

        if(message.getVideos() == null) {
            message.setVideos(new ArrayList<>());
        }
        if(video != null) {
            message.getVideos().add(video);
        }

        MediaType type = getMessageMediaType(sendMessageInGroupDto);
        message.setMediaType(type);

        message.setContent(sendMessageInGroupDto.getMessage());
        message.setCreatedAt(LocalDateTime.now());

        // send this message to the receivers
        if(group.getMessages() == null) {
            group.setMessages(new HashSet<>());
        }
        group.getMessages().add(message);
        messageRepository.save(message);
        groupRepository.save(group);

        return "message sent";

    }

    @Override
    @Transactional
    public Page<GroupMessagesGetterDto> getGroupMessages(String token, Long groupId, int page, int size) throws InvalidTokenException, GroupNotFoundException, UnauthorizedUserException {
        User user = validateAndGetUser(token);
        Group group = groupRepository.findByGroupId(groupId)
                .orElseThrow(() -> new GroupNotFoundException("group does not exist"));

        boolean isGroupMember = group.getGroupMemberships().stream()
                .anyMatch(membership -> membership.getUser().equals(user));

        if(!isGroupMember) {
            throw new UnauthorizedUserException("Only group members can get messages");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Message> paginatedMessages = messageRepository.findByGroup(group, pageable);

        return paginatedMessages.map(this::getGroupMessages);
    }

    private GroupMessagesGetterDto getGroupMessages(Message message) {
        GroupMessagesGetterDto msg = new GroupMessagesGetterDto();
        msg.setPublishDate(message.getCreatedAt());
        msg.setMessage(message.getContent());
        msg.setUserName(message.getSender().getFirstName()+" "+message.getSender().getLastName());

        if(message.getImages() != null) {
            List<String> imgs = message.getImages()
                    .stream()
                    .map(Image::getImageUrl)
                    .collect(Collectors.toList());
            msg.setImages(imgs);
        }
        if(message.getVideos() != null) {
            List<String> vids = message.getVideos()
                    .stream()
                    .map(Video::getVideoLink)
                    .collect(Collectors.toList());
            msg.setVideos(vids);
        }
        return msg;
    }

    private Video createVideo(SendMessageInGroupDto sendMessageInGroupDto, Message message) {
        if(sendMessageInGroupDto.getVideoUrl() != null && !sendMessageInGroupDto.getVideoUrl().isEmpty()) {
            Video newVideo = new Video();
            newVideo.setCreatedAt(LocalDateTime.now());
            newVideo.setVideoLink(sendMessageInGroupDto.getVideoUrl());
            newVideo.setMessage(message);

            return videoRepository.save(newVideo);
        }
        return null;
    }

    private Image createImage(SendMessageInGroupDto sendMessageInGroupDto, Group group, Message message) {
        if(sendMessageInGroupDto.getImageUrl() != null && !sendMessageInGroupDto.getImageUrl().isEmpty()) {
            Image newImage = new Image();
            newImage.setCreatedAt(LocalDateTime.now());
            newImage.setImageUrl(sendMessageInGroupDto.getImageUrl());
            newImage.setGroup(group);
            newImage.setMessage(message);

            return imageRepository.save(newImage);
        }
        return null;
    }

    private MediaType getMessageMediaType(SendMessageInGroupDto sendMessageInGroupDto) {
        if(sendMessageInGroupDto.getImageUrl() == null && sendMessageInGroupDto.getVideoUrl() == null) {
            return MediaType.TEXT;
        }else if(sendMessageInGroupDto.getMessage() == null || sendMessageInGroupDto.getMessage().isEmpty())  {
            return MediaType.MULTIMEDIA;
        }
        return MediaType.TEXT_WITH_MULTIMEDIA;
    }

    private void createNotificationForUser(User admin, User user, Group group, GroupNotificationType type) {
        Notification notification = new Notification();
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUser(user);
        notification.setStatus(NotificationStatus.UNREAD);
        notification.setMessage(buildNotificationMessageForUser(admin, user, group, type));
        notificationRepository.save(notification);
    }

    private String buildNotificationMessageForUser(User admin, User user, Group group, GroupNotificationType type) {
        return switch (type) {
            case JOIN_REQUEST_APPROVED -> admin.getFirstName()+" "+admin.getLastName()+" has accepted your request";
            case USER_LEFT_GROUP -> "User " + admin.getFirstName() + " " + admin.getLastName() + " has left the group";
            case PROMOTION -> admin.getFirstName()+" "+admin.getLastName()+" has made you an admin of the group "+
                    group.getGroupName();
            case ADMIN_REMOVED_USER -> admin.getFirstName()+" "+admin.getLastName()+" has removed you from the group "+group.getGroupName();
            case GROUP_MEMBER_ADD_USERS -> admin.getFirstName()+" "+admin.getLastName()+" added you to the group "+group.getGroupName();
            case GROUP_DELETED -> admin.getFirstName()+" "+admin.getLastName()+" has deleted the group "+group.getGroupName();
            default -> throw new IllegalArgumentException("invalid type");
        };
    }

    private void createNotificationForAdmin(User user, List<User> admins, Group group, GroupNotificationType type) {
        for(User admin : admins) {
            Notification notification = new Notification();
            notification.setCreatedAt(LocalDateTime.now());
            notification.setMessage(buildNotificationMessageForAdmin(user, admin, group, type));
            notification.setUser(admin);
            notification.setStatus(NotificationStatus.UNREAD);
            notificationRepository.save(notification);
        }
    }

    private String buildNotificationMessageForAdmin(User user, User admin, Group group, GroupNotificationType type) {
        return switch (type) {
            case JOIN_REQUEST_APPROVAL -> user.getFirstName()+" "+user.getLastName() +" wants to join the group "+group.getGroupName();
            case USER_LEFT_GROUP -> user.getFirstName()+" "+user.getLastName() +" has left the group "+group.getGroupName();
            default ->  throw new IllegalArgumentException("invalid type");
        };
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
