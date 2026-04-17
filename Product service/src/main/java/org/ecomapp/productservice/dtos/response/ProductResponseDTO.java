package org.ecomapp.productservice.dtos.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.ecomapp.productservice.enums.ProductStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private Double basePrice;
    private ProductStatus status;
    private Long categoryId;
//    private String categoryName;
    private Long sellerId;
//    private String sellerStoreName;
    private LocalDateTime createdAt;
//    private String thumbnailImage;
}
