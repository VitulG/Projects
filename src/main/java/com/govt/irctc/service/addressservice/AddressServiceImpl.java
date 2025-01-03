package com.govt.irctc.service.addressservice;

import com.govt.irctc.exceptions.addressexceptions.AddressCreationException;
import com.govt.irctc.model.Address;
import com.govt.irctc.model.User;
import com.govt.irctc.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AddressServiceImpl implements AddressService{
    private final AddressRepository addressRepository;

    @Autowired
    public AddressServiceImpl(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    public Address createAddress(User user, int houseNumber, String streetName, String city, String state, String country, String pinCode) throws AddressCreationException {

        Optional<Address> optionalAddress = addressRepository.findByHouseNumber(houseNumber);

        if(optionalAddress.isPresent()) {
            throw new AddressCreationException("Address already exists");
        }

        Address address = new Address();
        address.setHouseNumber(houseNumber);
        address.setStreet(streetName);
        address.setCity(city);
        address.setState(state);
        address.setCountry(country);
        address.setPinCode(pinCode);
        address.setUser(user);

        return addressRepository.save(address);
    }
}
