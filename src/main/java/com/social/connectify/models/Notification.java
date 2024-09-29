package com.social.connectify.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.social.connectify.dto.NotificationDto;
import com.social.connectify.enums.NotificationStatus;
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
public class Notification extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JsonIgnore
    private User user;

    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    public NotificationDto convertToDto() {
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setNotice(this.message);
        notificationDto.setStatus(status.toString().toLowerCase());
        return notificationDto;
    }
}
