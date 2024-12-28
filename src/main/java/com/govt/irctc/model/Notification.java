package com.govt.irctc.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Notification extends BaseModel {
    private String title;
    private String message;
    private boolean isRead;
    @ManyToOne
    private User user;
}
