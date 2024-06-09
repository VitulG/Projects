package com.swag.app.service;


import com.swag.app.dto.ContactDto;
import com.swag.app.dto.PersonDto;

import java.util.List;

public interface ContactService {
    List<ContactDto> getAllContactByUser(String user);
    Long addContact(ContactDto contactDto);
    List<ContactDto> getAllContacts();
    List<PersonDto> getAllUsers();
}
