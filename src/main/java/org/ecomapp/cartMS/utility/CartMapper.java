package org.ecomapp.cartMS.utility;

import org.ecomapp.cartMS.dtos.response.CartItemResponseDTO;
import org.ecomapp.cartMS.dtos.response.CartResponseDTO;
import org.ecomapp.cartMS.models.Cart;
import org.ecomapp.cartMS.models.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "userId", source = "user.id")
    CartResponseDTO cartToCartResDto(Cart cart);

    @Mapping(target = "productVariantId", source = "productVariant.id")
    @Mapping(target = "variantSku", source = "productVariant.sku")
    @Mapping(target = "price", source = "productVariant.price")
    @Mapping(target = "productName", source = "productVariant.product.name")
    @Mapping(target = "subtotal", expression = "java(cartItem.getQuantity() * cartItem.getProductVariant().getPrice())")
//    cartItem.getProductVariant().getPrice() * cartItem.getQuantity()
    CartItemResponseDTO cartItemToCartItemResDto(CartItem cartItem);

}
