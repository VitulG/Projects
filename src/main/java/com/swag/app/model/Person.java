package com.swag.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.swag.app.dto.PersonDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Person extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "person", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private List<Contact> contacts;

    public PersonDto convertToDto() {
        PersonDto dto = new PersonDto();
        dto.setName(getName());
        return dto;
    }
}
