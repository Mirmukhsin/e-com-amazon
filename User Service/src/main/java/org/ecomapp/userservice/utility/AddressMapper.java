package org.ecomapp.userservice.utility;

import org.ecomapp.userservice.dtos.request.AddressRequestDTO;
import org.ecomapp.userservice.dtos.response.AddressResponseDTO;
import org.ecomapp.userservice.dtos.response.AddressSnapshotDTO;
import org.ecomapp.userservice.models.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    Address addressReqDtoToAddress(AddressRequestDTO addressRequestDTO);

    @Mapping(target = "userId", source = "user.id")
    AddressResponseDTO addressToAddressResDto(Address address);
}
