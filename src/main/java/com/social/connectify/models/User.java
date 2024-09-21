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

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "user_friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private Set<User> userFriends;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "user_groups",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    private Set<Group> userGroups;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.REMOVE)
    private List<Message> sentMessages;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "user_received_messages",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "message_id")
    )
    private List<Message> receivedMessages;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Friendship> friendshipsSent;

    @OneToMany(mappedBy = "friend", cascade = CascadeType.ALL)
    private Set<Friendship> friendshipsReceived;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Notification> notifications;

    @ManyToMany
    @JoinTable(
            name = "user_followers",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "follower_id")
    )
    private Set<User> followers;

    @ManyToMany
    @JoinTable(
            name = "user_following",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "following_id")
    )
    private Set<User> following;

}
