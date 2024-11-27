package com.example.personal.EventSphere.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateEventRequestDto {
    private String eventTitle;
    private String eventDescription;
    private Date from;
    private Date to;
    private String location;
}
