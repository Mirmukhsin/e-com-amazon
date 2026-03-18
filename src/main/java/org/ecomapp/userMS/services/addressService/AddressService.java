package org.ecomapp.userMS.services.addressService;

import org.ecomapp.userMS.dtos.request.AddressRequestDTO;
import org.ecomapp.userMS.dtos.response.AddressResponseDTO;

import java.util.List;

public interface AddressService {

    List<AddressResponseDTO> getUserAddresses();

    AddressResponseDTO createAddress(AddressRequestDTO addressDTO);

    AddressResponseDTO updateAddress(Long addressId, AddressRequestDTO updateDTO);

    void setDefaultAddress(Long addressId);

    void deleteAddress(Long addressId);
}
