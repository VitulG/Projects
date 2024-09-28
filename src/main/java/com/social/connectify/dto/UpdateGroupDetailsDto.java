package com.social.connectify.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGroupDetailsDto {
    private Long groupId;
    private String newGroupName;
    private String newGroupDescription;
    private String newImageUrl;
}
