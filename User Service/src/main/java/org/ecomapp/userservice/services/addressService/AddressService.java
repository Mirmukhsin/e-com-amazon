package org.ecomapp.userservice.services.addressService;

import org.ecomapp.userservice.dtos.request.AddressRequestDTO;
import org.ecomapp.userservice.dtos.response.AddressResponseDTO;

import java.util.List;

public interface AddressService {

    List<AddressResponseDTO> getUserAddresses(Long userId);

    AddressResponseDTO getById(Long userId, Long addressId);

    AddressResponseDTO createAddress(Long userId, AddressRequestDTO addressDTO);

    AddressResponseDTO updateAddress(Long userId, Long addressId, AddressRequestDTO updateDTO);

    void setDefaultAddress(Long userId, Long addressId);

    void deleteAddress(Long userId, Long addressId);
}
