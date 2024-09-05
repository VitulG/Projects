package com.social.connectify.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String password;
    private Date dateOfBirth;
    private String gender;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Token> userTokens;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Post> userPosts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Like> userLikes;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Comment> userComments;


}
