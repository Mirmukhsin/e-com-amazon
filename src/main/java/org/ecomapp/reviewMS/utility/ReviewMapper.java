package org.ecomapp.reviewMS.utility;

import org.ecomapp.reviewMS.dtos.response.ReviewResponseDTO;
import org.ecomapp.reviewMS.models.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "buyerId", source = "buyer.id")
    @Mapping(target = "orderItemId", source = "orderItem.id")
    @Mapping(target = "productId", source = "product.id")
    ReviewResponseDTO reviewToReviewResDto(Review review);
}
