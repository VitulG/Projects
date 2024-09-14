package com.social.connectify.models;

import com.social.connectify.dto.FriendshipDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Friendship extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long friendshipId;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private User user;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private User friend;

    @Enumerated(EnumType.STRING)
    private FriendRequestStatus status;

    private LocalDateTime requestSentDate;
    private LocalDateTime requestAcceptedDate;

    public FriendshipDto convertToFriendshipDto() {
        FriendshipDto friendshipDto = new FriendshipDto();
        friendshipDto.setFriendFirstName(user.getFirstName());
        friendshipDto.setFriendLastName(user.getLastName());

        return friendshipDto;
    }
}
