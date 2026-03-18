package org.ecomapp.userMS.utility;

import org.ecomapp.userMS.dtos.request.SellerRequestDTO;
import org.ecomapp.userMS.dtos.response.SellerResponseDTO;
import org.ecomapp.userMS.models.SellerProfile;
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
