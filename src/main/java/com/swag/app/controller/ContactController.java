package com.swag.app.controller;

import com.swag.app.dto.ContactDto;
import com.swag.app.dto.PersonDto;
import com.swag.app.service.ContactService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ContactController {

    private final ContactService contactService;

    @Autowired
    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @RequestMapping(value="/contacts/{user}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<ContactDto>> getAllNumbers(@PathVariable String user) {
        List<ContactDto> contactDto = contactService.getAllContactByUser(user);
        return new ResponseEntity<>(contactDto, HttpStatus.OK);
    }

    @PostMapping("/add-contact")
    @ResponseBody
    public ResponseEntity<String> addContact(@RequestBody ContactDto contactDto) {
        Long savedKey = contactService.addContact(contactDto);
        return new ResponseEntity<>("Contact has been saved with id: "+savedKey, HttpStatus.CREATED);
    }

    @GetMapping("/all-contacts")
    @ResponseBody
    public ResponseEntity<List<ContactDto>> getAllContacts() {
        List<ContactDto> contacts = contactService.getAllContacts();
        return new ResponseEntity<>(contacts, HttpStatus.OK);
    }

    @GetMapping("/get-users")
    @ResponseBody
    public ResponseEntity<List<PersonDto>> getAllUsers() {
        List<PersonDto> users = contactService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

}
