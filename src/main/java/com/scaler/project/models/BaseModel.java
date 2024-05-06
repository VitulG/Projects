package com.scaler.project.models;

import java.util.Date;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public class BaseModel {

	private Date createdAt;
	private Date updatedAt;
	private boolean isDeleted;
}
