package org.ecomapp.reviewMS.dtos.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReviewResponseDTO {

    private Long id;
    private Long buyerId;
    private Long productId;
    private Long orderItemId;
    private Integer rating;
    private String comment;
}
