package org.ecomapp.userservice.services.addressService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.ecomapp.userservice.dtos.request.AddressRequestDTO;
import org.ecomapp.userservice.dtos.response.AddressResponseDTO;
import org.ecomapp.userservice.exceptionHandling.customExceptions.ResourceNotFoundException;
import org.ecomapp.userservice.exceptionHandling.customExceptions.UnAuthorizedException;
import org.ecomapp.userservice.models.Address;
import org.ecomapp.userservice.models.User;
import org.ecomapp.userservice.repositories.AddressRepository;
import org.ecomapp.userservice.repositories.UserRepository;
import org.ecomapp.userservice.utility.AddressMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;

    @Transactional
    @Override
    public AddressResponseDTO createAddress(Long userId, AddressRequestDTO addressReqDTO) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (addressReqDTO.getIsDefault()) {
            addressRepository.findByUserIdAndIsDefault(userId, true)
                    .ifPresent(currentAddress -> {
                        currentAddress.setIsDefault(false);
                        addressRepository.save(currentAddress);
                    });
        }

        Address address = addressMapper.addressReqDtoToAddress(addressReqDTO);
        address.setUser(user);
        addressRepository.save(address);
        return addressMapper.addressToAddressResDto(address);
    }

    @Override
    public AddressResponseDTO updateAddress(Long userId, Long addressId, AddressRequestDTO updateDTO) {
        Address address = addressRepository.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        if (!address.getUser().getId().equals(userId)) {
            throw new UnAuthorizedException("Unauthorized");
        }

        if (updateDTO.getLabel() != null) {
            address.setLabel(updateDTO.getLabel());
        }
        if (updateDTO.getStreet() != null && !updateDTO.getStreet().isBlank()) {
            address.setStreet(updateDTO.getStreet());
        }

        if (updateDTO.getCity() != null && !updateDTO.getCity().isBlank()) {
            address.setCity(updateDTO.getCity());
        }

        if (updateDTO.getState() != null && !updateDTO.getState().isBlank()) {
            address.setState(updateDTO.getState());
        }

        if (updateDTO.getZip() != null && !updateDTO.getZip().isBlank()) {
            address.setZip(updateDTO.getZip());
        }

        if (updateDTO.getCountry() != null && !updateDTO.getCountry().isBlank()) {
            address.setCountry(updateDTO.getCountry());
        }

        if (updateDTO.getIsDefault() != null) {
            address.setIsDefault(updateDTO.getIsDefault());
        }
        addressRepository.save(address);
        return addressMapper.addressToAddressResDto(address);
    }

    @Override
    public void deleteAddress(Long userId, Long addressId) {
        Address address = addressRepository.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        if (!address.getUser().getId().equals(userId)) {
            throw new UnAuthorizedException("Unauthorized");
        } else {
            addressRepository.deleteById(addressId);
        }

    }

    @Override
    public List<AddressResponseDTO> getUserAddresses(Long userId) {
        return addressRepository.findAllByUserId(userId).stream().map(addressMapper::addressToAddressResDto).toList();
    }

    @Override
    public AddressResponseDTO getById(Long userId, Long addressId) {
        Address address = addressRepository.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        if (!address.getUser().getId().equals(userId)) {
            throw new UnAuthorizedException("Unauthorized");
        } else {
            return addressMapper.addressToAddressResDto(address);
        }
    }

    @Transactional
    @Override
    public void setDefaultAddress(Long userId, Long addressId) {

        addressRepository.findByUserIdAndIsDefault(userId, true)
                .ifPresent(currentDefault -> {
                    currentDefault.setIsDefault(false);
                    addressRepository.save(currentDefault);
                });

        Address newAddress = addressRepository.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        if (!newAddress.getUser().getId().equals(userId)) {
            throw new UnAuthorizedException("Address does not belong to this user");
        }

        newAddress.setIsDefault(true);
        addressRepository.save(newAddress);
    }
}
