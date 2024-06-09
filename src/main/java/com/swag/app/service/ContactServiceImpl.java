package com.swag.app.service;

import com.swag.app.dto.ContactDto;
import com.swag.app.dto.PersonDto;
import com.swag.app.model.Contact;
import com.swag.app.model.Person;
import com.swag.app.repository.ContactRepository;
import com.swag.app.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class ContactServiceImpl implements ContactService{

    private ContactRepository contactRepository;
    private PersonRepository personRepository;

    @Autowired
    public ContactServiceImpl(ContactRepository contactRepository,
                              PersonRepository personRepository) {
        this.contactRepository = contactRepository;
        this.personRepository = personRepository;
    }

    @Override
    public List<ContactDto> getAllContactByUser(String user) {
        List<Contact> contacts = contactRepository.findAllBy(user);

        List<ContactDto> contactsDto = new ArrayList<ContactDto>();
        for(Contact contact : contacts) {
            contactsDto.add(contact.convertToContactDto());
        }
        return contactsDto;
    }

    @Override
    public Long addContact(ContactDto contactDto) {
        Person personAvailable = personRepository.findBy(contactDto.getUser());

        if(personAvailable == null) {
            Person person = new Person();
            person.setName(contactDto.getUser());
            person.setCreatedAt(LocalDateTime.now());
            personAvailable = person;
            personRepository.save(person);
        }
        Contact contact = new Contact();
        contact.setContactName(contactDto.getContactName());
        contact.setContactPhone(contactDto.getPhoneNumber());
        contact.setPerson(personAvailable);
        contact.setCreatedAt(LocalDateTime.now());
        contactRepository.save(contact);

        return contact.getId();

    }

    @Override
    public List<ContactDto> getAllContacts() {
        List<Contact> contacts = contactRepository.findAll();

        List<ContactDto> contactsDto = new ArrayList<>();
        for(Contact contact : contacts) {
            contactsDto.add(contact.convertToContactDto());
        }
        return contactsDto;
    }

    @Override
    public List<PersonDto> getAllUsers() {
        List<Person> users = personRepository.findAll();
        List<PersonDto> personsDto = new ArrayList<>();

        for(Person person : users) {
            personsDto.add(person.convertToDto());
        }
        return personsDto;
    }
}
