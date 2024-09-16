package com.social.connectify.dto;

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
public class SentMessageDto {
    private List<String> recipients;
    private String message;
}
