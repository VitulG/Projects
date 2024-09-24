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
public class GroupMembership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupMembershipId;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private User user;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Group group;

    @Enumerated(EnumType.STRING)
    private GroupUserRole role;
}
