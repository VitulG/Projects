package com.social.connectify.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageInGroupDto {
    private Long groupId;
    private String message;
    private String imageUrl;
    private String videoUrl;
}
