package org.ecomapp.cartservice.utility;

import org.ecomapp.cartservice.dtos.clientsDTOs.CartItemDTO;
import org.ecomapp.cartservice.dtos.response.CartItemResponseDTO;
import org.ecomapp.cartservice.dtos.response.CartResponseDTO;
import org.ecomapp.cartservice.dtos.clientsDTOs.ProductVariantDTO;
import org.ecomapp.cartservice.models.Cart;
import org.ecomapp.cartservice.models.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "totalItems", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    CartResponseDTO cartResponseFromCart(Cart cart);

    @Mapping(target = "id", source = "cartItem.id")
    @Mapping(target = "productVariantId", source = "cartItem.productVariantId")
    @Mapping(target = "variantSku", source = "variantDTO.sku")
    @Mapping(target = "price", source = "variantDTO.price")
    @Mapping(target = "productName", source = "variantDTO.productName")
    @Mapping(target = "subtotal",
            expression = "java(cartItem.getQuantity() * variantDTO.getPrice())")
    CartItemResponseDTO cartItemResponseFromCartItem(CartItem cartItem, ProductVariantDTO variantDTO);

    @Mapping(target = "cartId", source = "cart.id")
    CartItemDTO cartItemDTOFromCartItem(CartItem cartItem);

}
