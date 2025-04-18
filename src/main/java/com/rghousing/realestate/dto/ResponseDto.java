package com.rghousing.realestate.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ResponseDto {
    private String message;
    private String errorMessage;
    private HttpStatus status;
}
