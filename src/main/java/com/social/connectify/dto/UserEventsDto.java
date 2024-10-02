package com.social.connectify.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserEventsDto {
    private String eventName;
    private String eventDescription;
    private String imageUrl;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}

