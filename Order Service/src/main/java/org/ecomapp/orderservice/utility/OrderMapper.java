package org.ecomapp.orderservice.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ecomapp.orderservice.dtos.clientsDTOs.OderDTOForPayment;
import org.ecomapp.orderservice.dtos.clientsDTOs.OrderItemDTOForReview;
import org.ecomapp.orderservice.dtos.clientsDTOs.SubOderDTOForPayment;
import org.ecomapp.orderservice.dtos.response.OrderItemResponseDTO;
import org.ecomapp.orderservice.dtos.response.OrderResponseDTO;
import org.ecomapp.orderservice.dtos.response.SubOrderResponseDTO;
import org.ecomapp.orderservice.dtos.clientsDTOs.addressDTOs.AddressDTO;
import org.ecomapp.orderservice.dtos.clientsDTOs.addressDTOs.AddressSnapshotDTO;
import org.ecomapp.orderservice.models.Order;
import org.ecomapp.orderservice.models.OrderItem;
import org.ecomapp.orderservice.models.SubOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    AddressSnapshotDTO addressSnapshotFromAddressDTO(AddressDTO addressDTO);


    @Mapping(target = "buyerId", source = "buyerId")
    @Mapping(target = "shippingAddress", source = "shippingAddress")
    @Mapping(target = "subOrders", ignore = true)
    OrderResponseDTO orderResDTOFromOrder(Order order);

    @Mapping(target = "productVariantId", source = "productVariantId")
    OrderItemResponseDTO orderItemResDTOFromOrderItem(OrderItem orderItem);

    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "sellerId", source = "sellerId")
    @Mapping(target = "items", ignore = true)
        // TODO: fetch via feign
    SubOrderResponseDTO subOrderResDTOFromSubOrder(SubOrder subOrder);

    default AddressSnapshotDTO map(String shippingAddress) {
        try {
            return OBJECT_MAPPER.readValue(shippingAddress, AddressSnapshotDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

//  TODO:  for payment

    OderDTOForPayment orderDTOForPayment(Order order);

    @Mapping(target = "buyerId", source = "order.buyerId")
    @Mapping(target = "orderId", source = "order.id")
    SubOderDTOForPayment subOrderDTOForPayment(SubOrder subOrder);

//  TODO:  for review

    @Mapping(target = "buyerId", source = "subOrder.order.buyerId")
    @Mapping(target = "subOrderId", source = "subOrder.id")
    OrderItemDTOForReview orderItemDTOForReview(OrderItem orderItem);
}
