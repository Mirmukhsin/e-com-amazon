package org.ecomapp.userMS.utility;

import org.ecomapp.userMS.dtos.request.AddressRequestDTO;
import org.ecomapp.userMS.dtos.response.AddressResponseDTO;
import org.ecomapp.userMS.dtos.response.AddressSnapshotDTO;
import org.ecomapp.userMS.models.Address;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    Address addressReqDtoToAddress(AddressRequestDTO addressRequestDTO);

    AddressResponseDTO addressToAddressResDto(Address address);

    AddressSnapshotDTO addressToAddressSnapDto(Address address);
}
