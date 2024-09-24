package com.social.connectify.services.GroupService;

import com.social.connectify.dto.GroupCreationDto;
import com.social.connectify.dto.GroupCreationException;
import com.social.connectify.exceptions.InvalidTokenException;
import com.social.connectify.models.*;
import com.social.connectify.repositories.*;
import com.social.connectify.validations.GroupCreationValidator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

@Service
public class GroupServiceImpl implements GroupService {
    private final TokenRepository tokenRepository;
    private final ImageRepository imageRepository;
    private final GroupMembershipRepository groupMembershipRepository;
    private final GroupRepository groupRepository;
    private final GroupCreationValidator groupCreationValidator;
    private final UserRepository userRepository;

    @Autowired
    public GroupServiceImpl(TokenRepository tokenRepository, ImageRepository imageRepository,
                            GroupMembershipRepository groupMembershipRepository, GroupRepository groupRepository,
                            GroupCreationValidator groupCreationValidator, UserRepository userRepository) {
        this.tokenRepository = tokenRepository;
        this.imageRepository = imageRepository;
        this.groupMembershipRepository = groupMembershipRepository;
        this.groupRepository = groupRepository;
        this.groupCreationValidator = groupCreationValidator;
        this.userRepository = userRepository;
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
