package org.ecomapp.userservice.clients;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "AUTH-SERVICE")
public interface AuthMSClient {
}
