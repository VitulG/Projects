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
public class PostDto {
    private String content;
    private Long numberOfTimesPostShared;
    private Long numberOfLikes;
    private Long numberOfComments;
    private List<Image> images;
    private List<Video> videos;
}
