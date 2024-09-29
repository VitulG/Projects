package com.social.connectify.services.GroupService;

import com.social.connectify.dto.*;
import com.social.connectify.exceptions.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface GroupService {
    String createGroup(String token, GroupCreationDto groupCreationDto) throws InvalidTokenException, GroupCreationException;
    String joinGroup(String token, Long groupId) throws InvalidTokenException, GroupNotFoundException;
    String respondRequest(String token, RespondGroupRequestDto respondGroupRequestDto) throws InvalidTokenException, GroupMembershipNotFoundException, UnauthorizedUserException;
    List<GroupMembersDto> getAllGroupMembers(String token, Long groupId) throws InvalidTokenException, GroupNotFoundException, UnauthorizedUserException;
    String makeUserAdmin(String token, Long groupId, Long userId) throws InvalidTokenException, UserNotFoundException, GroupNotFoundException, UnauthorizedUserException;
    String leaveGroup(String token, Long groupId) throws InvalidTokenException, GroupNotFoundException, UnauthorizedUserException;
    String removeAMember(String token, Long groupId, Long userId) throws InvalidTokenException, GroupNotFoundException, UserNotFoundException, UnauthorizedUserException;
    String addMembers(String token, AddMembersDto addMembersDto) throws InvalidTokenException, GroupNotFoundException, UnauthorizedUserException;
    String updateGroupDetails(String token, UpdateGroupDetailsDto detailsDto) throws InvalidTokenException, GroupNotFoundException, UnauthorizedUserException;
    String deleteGroup(String token, Long groupId) throws InvalidTokenException, GroupNotFoundException, UnauthorizedUserException;
    String sendGroupMessage(String token, SendMessageInGroupDto sendMessageInGroupDto) throws InvalidTokenException, GroupNotFoundException, UnauthorizedUserException;
    Page<GroupMessagesGetterDto> getGroupMessages(String token, Long groupId, int page, int size) throws InvalidTokenException, GroupNotFoundException, UnauthorizedUserException;
}
