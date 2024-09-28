package com.social.connectify.models;

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
@Table(name = "connectify_group")
public class Group extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupId;

    @Column(unique = true)
    private String groupName;

    private String groupDescription;
    private GroupStatus groupStatus;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "group_user",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users;

    @OneToMany(mappedBy = "group", cascade = CascadeType.REMOVE)
    private List<Post> posts;

    @ManyToMany(mappedBy = "groups")
    private Set<Message> messages;

    @OneToMany(cascade = CascadeType.REMOVE)
    private List<GroupMembership> groupMemberships;

    @OneToOne
    private Image groupImage;
}
