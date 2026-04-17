package org.ecomapp.userservice.utility;

import org.ecomapp.userservice.dtos.request.SellerRequestDTO;
import org.ecomapp.userservice.dtos.response.SellerResponseDTO;
import org.ecomapp.userservice.models.SellerProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SellerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "totalSales", ignore = true)
    SellerProfile sellerReqDtoToSeller(SellerRequestDTO sellerRequestDTO);

    SellerResponseDTO sellerToSellerResDto(SellerProfile sellerProfile);
}
