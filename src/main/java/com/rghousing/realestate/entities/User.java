package com.rghousing.realestate.entities;

import com.rghousing.realestate.enums.UserType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseModel {
    @Column(nullable = false)
    private String userName;

    @Column(unique = true, nullable = false)
    private String userPassword;

    @Column(nullable = false)
    private String userPhoneNumber;

    private String userAddress;

    @Column(unique = true, nullable = false)
    private String userEmail;

    @Enumerated(EnumType.STRING)
    private UserType userType;

}
