package com.social.connectify.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.social.connectify.enums.MediaType;
import com.social.connectify.enums.MessageStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;
    private String content;

    @Enumerated(EnumType.STRING)
    private MessageStatus messageStatus;

    @Enumerated(EnumType.STRING)
    private MediaType mediaType;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JsonIgnore
    private User sender;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JsonIgnore
    private Set<User> receivers;

    // optional attachments
    @OneToMany(mappedBy = "message", cascade = CascadeType.REMOVE)
    private List<Image> images;
    @OneToMany(mappedBy = "message", cascade = CascadeType.REMOVE)
    private List<Video> videos;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "message_group", // Join table for message and groups
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    private List<Group> groups;

}
