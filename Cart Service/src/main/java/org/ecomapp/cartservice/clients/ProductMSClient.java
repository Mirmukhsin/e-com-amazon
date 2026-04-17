package org.ecomapp.cartservice.clients;

import org.ecomapp.cartservice.dtos.clientsDTOs.ProductVariantDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Set;

@FeignClient(name = "PRODUCT-SERVICE")
public interface ProductMSClient {

    @GetMapping("/products/variants/{variantId}")
    ProductVariantDTO getById(@PathVariable Long variantId);

    @PostMapping("/products/variants/list")
    List<ProductVariantDTO> getAllByIds(@RequestBody Set<Long> variantIds);
}
