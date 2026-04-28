package org.ecomapp.orderservice.clients;

import org.ecomapp.orderservice.dtos.clientsDTOs.ProductVariantDTO;
import org.ecomapp.orderservice.dtos.clientsDTOs.ReserveStockRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Set;

@FeignClient(name = "PRODUCT-SERVICE")
public interface ProductMSClient {

    @PostMapping("/products/variants/list")
    List<ProductVariantDTO> getAllByIds(@RequestBody Set<Long> variantIds);

    @PostMapping("/products/variants/reserve")
    void reserveStock(@RequestBody List<ReserveStockRequest> request);
}
