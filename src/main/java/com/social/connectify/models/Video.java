package com.social.connectify.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Video extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long videoId;

    private String videoLink;

    @ManyToOne(cascade = CascadeType.PERSIST, optional = true)
    private Post post;

    @ManyToOne(cascade = CascadeType.PERSIST, optional = true)
    private Message message;
}
