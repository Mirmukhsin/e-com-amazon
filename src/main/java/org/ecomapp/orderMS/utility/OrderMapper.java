package org.ecomapp.orderMS.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ecomapp.orderMS.dtos.response.OrderItemResponseDTO;
import org.ecomapp.orderMS.dtos.response.OrderResponseDTO;
import org.ecomapp.orderMS.dtos.response.SubOrderResponseDTO;
import org.ecomapp.orderMS.models.Order;
import org.ecomapp.orderMS.models.OrderItem;
import org.ecomapp.orderMS.models.SubOrder;
import org.ecomapp.userMS.dtos.response.AddressSnapshotDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    ObjectMapper OBJECT_MAPPER = new ObjectMapper();


    @Mapping(target = "buyerId", source = "buyer.id")
    @Mapping(target = "shippingAddress", source = "shippingAddress")
    OrderResponseDTO orderToOrderResDto(Order order);

    @Mapping(target = "productVariantId", source = "productVariant.id")
    OrderItemResponseDTO orderItemToOrderItemResDto(OrderItem orderItem);

    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "sellerId", source = "seller.id")
    @Mapping(target = "sellerStoreName", source = "seller.fullName")
    SubOrderResponseDTO subOrderToSubOrderResDto(SubOrder subOrder);

    default AddressSnapshotDTO map(String shippingAddress) {
        try {
            return OBJECT_MAPPER.readValue(shippingAddress, AddressSnapshotDTO.class);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
