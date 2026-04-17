package org.ecomapp.productservice.utility;

import org.ecomapp.productservice.dtos.ProductVariantDTO;
import org.ecomapp.productservice.dtos.request.ProductRequestDTO;
import org.ecomapp.productservice.dtos.request.ProductVariantRequestDTO;
import org.ecomapp.productservice.dtos.response.ProductImageResponseDTO;
import org.ecomapp.productservice.dtos.response.ProductResponseDTO;
import org.ecomapp.productservice.dtos.response.ProductVariantResponseDTO;
import org.ecomapp.productservice.models.Product;
import org.ecomapp.productservice.models.ProductImage;
import org.ecomapp.productservice.models.ProductVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {


    @Mapping(target = "categoryId", source = "categoryId")
    @Mapping(target = "sellerId", source = "sellerId")
    ProductResponseDTO productResDTOFromProduct(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "sellerId", ignore = true)
    Product productFromProductReqDTO(ProductRequestDTO productRequestDTO);

    ProductImageResponseDTO productImgResDTOFromProductImg(ProductImage productImage);

    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "sellerId", source = "product.sellerId")
    ProductVariantResponseDTO prodVariantResDTOFromProdVariant(ProductVariant productVariant);


    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "sellerId", source = "product.sellerId")
    ProductVariantDTO prodVariantDTOFromProdVariant(ProductVariant productVariant);

    @Mapping(target = "id", ignore = true )
    @Mapping(target = "product", ignore = true )
    ProductVariant prodVariantFromProdVariantReqDTO(ProductVariantRequestDTO productVariantRequestDTO);
}
