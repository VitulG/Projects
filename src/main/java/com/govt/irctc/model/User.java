package com.govt.irctc.model;

import com.govt.irctc.dto.BookingDto;
import com.govt.irctc.dto.UserDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseModel{

    private String userName;
    private String userEmail;

    private int userAge;
    private String userGender;
    private Long userPhoneNumber;
    private String hashedPassword;
    private String profilePictureUrl;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Address> userAddresses;

    private Date userDob;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.REFRESH})
    private List<Role> userRoles;

    @OneToMany(mappedBy = "userTokens")
    private List<Token> userTokens;

    @OneToMany(mappedBy = "userBookings", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<Booking> userBookings;

}
