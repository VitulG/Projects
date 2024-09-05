package com.social.connectify.dto;

import com.social.connectify.models.Image;
import com.social.connectify.models.Video;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {
    private String content;
    private List<String> imageUrl;
    private List<String> videoUrl;
    private Long numberOfLikes;
    private Long numberOfComments;
    private Long numberOfTimesPostShared;
}
