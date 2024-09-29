package com.social.connectify.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupMessagesGetterDto {
    private String userName;
    private String message;
    private LocalDateTime publishDate;
    // if any
    private List<String> images;
    private List<String> videos;
}
