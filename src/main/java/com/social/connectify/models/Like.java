package com.social.connectify.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "user_like")
public class Like extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JsonIgnore
    private Post post;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JsonIgnore
    private User user;

}
