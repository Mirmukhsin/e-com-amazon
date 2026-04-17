package org.ecomapp.userservice.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "AUTH-SERVICE")
public interface AuthMSClient {
    @DeleteMapping("/auth")
    void disableUser(@RequestHeader("X-User-Id") Long userId);
}
