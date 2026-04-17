package org.ecomapp.reviewservice.utility;

import org.ecomapp.reviewservice.dtos.response.ReviewResponseDTO;
import org.ecomapp.reviewservice.models.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "buyerId", source = "buyerId")
    @Mapping(target = "orderItemId", source = "orderItemId")
    @Mapping(target = "productId", source = "productId")
    ReviewResponseDTO reviewToReviewResDto(Review review);
}
