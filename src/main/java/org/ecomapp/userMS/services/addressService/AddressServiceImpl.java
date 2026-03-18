package org.ecomapp.userMS.services.addressService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.ecomapp.exceptionHandling.customExceptions.ResourceNotFoundException;
import org.ecomapp.exceptionHandling.customExceptions.UnAuthorizedException;
import org.ecomapp.securityMS.utility.SecurityUtils;
import org.ecomapp.userMS.dtos.request.AddressRequestDTO;
import org.ecomapp.userMS.dtos.response.AddressResponseDTO;
import org.ecomapp.userMS.models.Address;
import org.ecomapp.userMS.models.User;
import org.ecomapp.userMS.repositories.AddressRepository;
import org.ecomapp.userMS.repositories.UserRepository;
import org.ecomapp.userMS.utility.AddressMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;
    private final SecurityUtils securityUtils;

    @Transactional
    @Override
    public AddressResponseDTO createAddress(AddressRequestDTO addressReqDTO) {
        Long currentUserId = securityUtils.getCurrentUserId();
        User user = userRepository.findById(currentUserId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (addressReqDTO.getIsDefault()) {
            addressRepository.findByUserIdAndIsDefault(currentUserId, true)
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
    public AddressResponseDTO updateAddress(Long addressId, AddressRequestDTO updateDTO) {
        Address address = addressRepository.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        if (!address.getUser().getId().equals(securityUtils.getCurrentUserId())) {
            throw new UnAuthorizedException("Unauthorized");
        }
        address.setLabel(updateDTO.getLabel());
        address.setStreet(updateDTO.getStreet());
        address.setCity(updateDTO.getCity());
        address.setState(updateDTO.getState());
        address.setZip(updateDTO.getZip());
        address.setCountry(updateDTO.getCountry());
        address.setIsDefault(updateDTO.getIsDefault());
        addressRepository.save(address);
        return addressMapper.addressToAddressResDto(address);
    }

    @Override
    public void deleteAddress(Long addressId) {
        Address address = addressRepository.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        if (!address.getUser().getId().equals(securityUtils.getCurrentUserId())) {
            throw new UnAuthorizedException("Unauthorized");
        } else {
            addressRepository.deleteById(addressId);
        }

    }

    @Override
    public List<AddressResponseDTO> getUserAddresses() {
        return addressRepository.findAllByUserId(securityUtils.getCurrentUserId()).stream().map(addressMapper::addressToAddressResDto).toList();
    }

    @Transactional
    @Override
    public void setDefaultAddress(Long addressId) {
        Long currentUserId = securityUtils.getCurrentUserId();
        Address currentAddress = addressRepository.findByUserIdAndIsDefault(currentUserId, true).orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        currentAddress.setIsDefault(false);
        addressRepository.save(currentAddress);

        Address newAddress = addressRepository.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        if (!newAddress.getUser().getId().equals(currentUserId)) {
            throw new UnAuthorizedException("Address does not belong to this user");
        }

        newAddress.setIsDefault(true);
        addressRepository.save(newAddress);
    }
}
