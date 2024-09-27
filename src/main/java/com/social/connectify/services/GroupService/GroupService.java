package com.social.connectify.services.GroupService;

import com.social.connectify.dto.AddMembersDto;
import com.social.connectify.dto.GroupCreationDto;
import com.social.connectify.dto.GroupMembersDto;
import com.social.connectify.dto.RespondGroupRequestDto;
import com.social.connectify.exceptions.*;

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
}
