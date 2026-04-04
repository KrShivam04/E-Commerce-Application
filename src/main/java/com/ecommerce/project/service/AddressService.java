package com.ecommerce.project.service;

import java.util.List;

import com.ecommerce.project.Payload.AddressDTO;
import com.ecommerce.project.model.User;

public interface AddressService {

    AddressDTO createAddress(AddressDTO addressDTO, User user);

    List<AddressDTO> getAddresses();

    AddressDTO getAddressById(Long addressId);

    List<AddressDTO> getUserAddresses(User user);

    AddressDTO updateAddress(Long addressId, AddressDTO addressDTO);

    String deleteAddress(Long addressId);

}
