package com.govt.irctc.service.addressservice;

import com.govt.irctc.exceptions.addressexceptions.AddressCreationException;
import com.govt.irctc.model.Address;
import com.govt.irctc.model.User;

public interface AddressService {
    public Address createAddress(User user, int houseNumber, String streetName, String city, String state,
                                 String country, String pinCode) throws AddressCreationException;
}
