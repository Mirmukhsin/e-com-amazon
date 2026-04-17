package org.ecomapp.userservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ecomapp.userservice.dtos.request.SellerRequestDTO;
import org.ecomapp.userservice.dtos.response.SellerResponseDTO;
import org.ecomapp.userservice.services.sellerService.SellerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/seller/profile")
@RequiredArgsConstructor
@Tag(name = "2. Seller Profile", description = "Seller profile management")
public class SellerController {
    private final SellerService sellerService;

    @Operation(summary = "Get seller profile - SELLER only")
    @GetMapping("/me")
    public ResponseEntity<SellerResponseDTO> getById(@RequestHeader("X-User-Id") Long sellerId) {
        return new ResponseEntity<>(sellerService.getSeller(sellerId), HttpStatus.OK);
    }

    @Operation(summary = "Create seller profile - SELLER only")
    @PostMapping("/me")
    public ResponseEntity<SellerResponseDTO> create(@RequestHeader("X-User-Id") Long userId, @Valid @RequestBody SellerRequestDTO sellerRequestDTO) {
        return new ResponseEntity<>(sellerService.createSeller(userId, sellerRequestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Update seller profile - SELLER only")
    @PutMapping("/me")
    public ResponseEntity<SellerResponseDTO> update(@RequestHeader("X-User-Id") Long sellerId, @Valid @RequestBody SellerRequestDTO sellerRequestDTO) {
        return new ResponseEntity<>(sellerService.updateSeller(sellerId, sellerRequestDTO), HttpStatus.OK);
    }

    @Operation(summary = "Update seller total sales - by order service")
    @PatchMapping("/me/sales")
    public ResponseEntity<Void> updateTotalSales(@RequestHeader("X-User-Id") Long sellerId, @RequestBody Integer totalSales) {
        sellerService.updateTotalSales(sellerId, totalSales);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
