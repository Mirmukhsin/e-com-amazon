package org.ecomapp.orderservice.clients;

import org.ecomapp.orderservice.dtos.clientsDTOs.addressDTOs.AddressDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "USER-SERVICE")
public interface UserMSClient {

    @GetMapping("/users/addresses/me/{addressId}")
    AddressDTO getById(@RequestHeader("X-User-Id") Long userId, @PathVariable Long addressId);
}
