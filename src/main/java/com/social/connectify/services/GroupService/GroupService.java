package com.social.connectify.services.GroupService;

import com.social.connectify.dto.GroupCreationDto;
import com.social.connectify.dto.GroupCreationException;
import com.social.connectify.exceptions.InvalidTokenException;

public interface GroupService {
    String createGroup(String token, GroupCreationDto groupCreationDto) throws InvalidTokenException, GroupCreationException;
}
