package org.ecomapp.productMS.utility;

import org.ecomapp.productMS.dtos.request.ProductRequestDTO;
import org.ecomapp.productMS.dtos.request.ProductVariantRequestDTO;
import org.ecomapp.productMS.dtos.response.ProductImageResponseDTO;
import org.ecomapp.productMS.dtos.response.ProductResponseDTO;
import org.ecomapp.productMS.dtos.response.ProductVariantResponseDTO;
import org.ecomapp.productMS.models.Product;
import org.ecomapp.productMS.models.ProductImage;
import org.ecomapp.productMS.models.ProductVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {


    @Mapping(target ="categoryId", source = "category.id")
    @Mapping(target ="categoryName", source = "category.name")
    @Mapping(target ="sellerId", source = "seller.id")
    @Mapping(target ="sellerStoreName", source = "seller.fullName")
    ProductResponseDTO productToProductResDto(Product product);

    Product productReqDtoToProduct(ProductRequestDTO productRequestDTO);

    ProductImageResponseDTO productImgToProductImgResDto(ProductImage productImage);

    ProductVariantResponseDTO prodVariantToProdVariantResDto(ProductVariant productVariant);

    ProductVariant prodVariantReqDtoToProdVariant(ProductVariantRequestDTO productVariantRequestDTO);
}
