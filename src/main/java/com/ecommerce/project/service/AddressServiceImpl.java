package com.ecommerce.project.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ecommerce.project.Exception.ResourceNotFoundException;
import com.ecommerce.project.Payload.AddressDTO;
import com.ecommerce.project.Repository.AddressRepository;
import com.ecommerce.project.Repository.UserRepository;
import com.ecommerce.project.model.Address;
import com.ecommerce.project.model.User;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO, User user) {
        Address address = modelMapper.map(addressDTO, Address.class);

        List<Address> addressList = user.getAddress();
        addressList.add(address);
        user.setAddress(addressList);

        address.setUser(user);
        Address savedAddress = addressRepository.save(address);

        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAddresses() {
        List<Address> address = addressRepository.findAll();
        List<AddressDTO> addressDTOs = address.stream().map(addr -> modelMapper.map(addr, AddressDTO.class)).collect(Collectors.toList());
        return addressDTOs;
    }

    @Override
    public AddressDTO getAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId).orElseThrow(()-> new ResourceNotFoundException("Address" , "addressId", addressId));
        return modelMapper.map(address, AddressDTO.class);       
    }

    @Override
    public List<AddressDTO> getUserAddresses(User user) {
        List<Address> address = user.getAddress();
        List<AddressDTO> addressDTOs = address.stream().map(addr -> modelMapper.map(addr, AddressDTO.class)).collect(Collectors.toList());
        return addressDTOs;   
    }

    @Override
    public AddressDTO updateAddress(Long addressId, AddressDTO addressDTO) {
        Address addressFromDatabase = addressRepository.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("Address" , "addressId", addressId));
        addressFromDatabase.setStreet(addressDTO.getStreet());
        addressFromDatabase.setCity(addressDTO.getCity());
        addressFromDatabase.setPinCode(addressDTO.getPinCode());
        addressFromDatabase.setBuilding(addressDTO.getBuilding());
        addressFromDatabase.setState(addressDTO.getState());
        addressFromDatabase.setCountry(addressDTO.getCountry());

        Address updatedAddress = addressRepository.save(addressFromDatabase);

        User user = addressFromDatabase.getUser();
        user.getAddress().removeIf(address -> address.getAddressId().equals(addressId));
        user.getAddress().add(updatedAddress);
        userRepository.save(user);

        return modelMapper.map(updatedAddress, AddressDTO.class);

    }

    @Override
    public String deleteAddress(Long addressId) {
        Address addressFromDatabase = addressRepository.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("Address" , "addressId", addressId));

        User user = addressFromDatabase.getUser();
        user.getAddress().removeIf(address -> address.getAddressId().equals(addressId));
        userRepository.save(user);


        addressRepository.delete(addressFromDatabase);
        return "Address Deleted successfully with address ID : " + addressId;

    }

}
