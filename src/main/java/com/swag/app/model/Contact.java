package com.swag.app.model;

import com.swag.app.dto.ContactDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Contact extends BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String contactName;
    private String contactPhone;

    @ManyToOne(cascade = CascadeType.REMOVE)
    private Person person;

    public ContactDto convertToContactDto() {
        ContactDto contactDto = new ContactDto();
        contactDto.setContactName(this.getContactName());
        contactDto.setPhoneNumber(this.getContactPhone());
        contactDto.setUser(person.getName());
        return contactDto;
    }
}
