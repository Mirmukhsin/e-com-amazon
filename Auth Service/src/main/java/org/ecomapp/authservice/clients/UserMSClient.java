package org.ecomapp.authservice.clients;

import org.ecomapp.authservice.dtos.request.CreateUserProfileDTO;
import org.ecomapp.authservice.dtos.response.UserResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "USER-SERVICE")
public interface UserMSClient {

    @PostMapping("/users/profile/create")
    UserResponseDTO create(@RequestBody CreateUserProfileDTO createUserProfileDTO);

    @GetMapping("/users/profile/me")
    UserResponseDTO getById(@RequestHeader("X-User-Id") Long userId);
}
