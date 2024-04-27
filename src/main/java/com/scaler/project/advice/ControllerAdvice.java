package com.scaler.project.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {
	
	@ExceptionHandler(NullPointerException.class)
	public ResponseEntity<String> handleNullPointer() {
		String msg = "The resource which you are trying to access is NULL";
		return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
	}
}
