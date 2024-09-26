package com.social.connectify.services.GroupService;

import com.social.connectify.dto.GroupCreationDto;
import com.social.connectify.dto.RespondGroupRequestDto;
import com.social.connectify.exceptions.*;

public interface GroupService {
    String createGroup(String token, GroupCreationDto groupCreationDto) throws InvalidTokenException, GroupCreationException;
    String joinGroup(String token, Long groupId) throws InvalidTokenException, GroupNotFoundException;
    String respondRequest(String token, RespondGroupRequestDto respondGroupRequestDto) throws InvalidTokenException, GroupMembershipNotFoundException, UnauthorizedUserException;
}
