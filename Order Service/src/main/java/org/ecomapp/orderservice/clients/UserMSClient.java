package org.ecomapp.orderservice.clients;

import org.ecomapp.orderservice.dtos.clientsDTOs.addressDTOs.AddressDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "USER-SERVICE")
public interface UserMSClient {

    @GetMapping("/users/addresses/me/{addressId}")
    AddressDTO getById(@RequestHeader("X-User-Id") Long userId, @PathVariable Long addressId);

    @PatchMapping("/users/seller/profile/me/sales")
    void updateSellerTotalSales(@RequestHeader("X-User-Id") Long sellerId, @RequestBody Integer totalSales);
}
