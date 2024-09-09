package com.social.connectify.models;

import com.social.connectify.dto.PostResponseDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post extends BaseModel {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long postId;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private User user;

    private String content;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy ="post", fetch = FetchType.LAZY)
    private List<Like> likes;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy="post")
    private List<Comment> comments;

    private Long numberOfTimesPostShared;
    private Long numberOfLikes;
    private Long numberOfComments;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy ="post")
    private List<Image> images;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy="post")
    private List<Video> videos;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Group group;


    public PostResponseDto convertToPostResponseDto() {
        PostResponseDto postResponseDto = new PostResponseDto();
        postResponseDto.setContent(this.getContent());
        postResponseDto.setImageUrl(this.getImages().stream().map(Image::getImageUrl).collect(Collectors.toList()));
        postResponseDto.setVideoUrl(this.getVideos().stream().map(Video::getVideoLink).collect(Collectors.toList()));
        postResponseDto.setNumberOfLikes((long) this.getLikes().size());
        postResponseDto.setNumberOfComments((long) this.getComments().size());
        postResponseDto.setNumberOfTimesPostShared(this.getNumberOfTimesPostShared());
        return postResponseDto;
    }

}
