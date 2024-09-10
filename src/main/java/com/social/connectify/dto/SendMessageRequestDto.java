package com.social.connectify.dto;

import com.social.connectify.models.Group;
import com.social.connectify.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequestDto {
    private String message;
    private String recipientEmailId;
    private String groupName;
    private String imageUrl;
    private String videoUrl;
}
