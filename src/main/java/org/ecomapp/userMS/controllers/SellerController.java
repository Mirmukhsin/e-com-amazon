package org.ecomapp.userMS.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ecomapp.userMS.dtos.request.SellerRequestDTO;
import org.ecomapp.userMS.dtos.response.SellerResponseDTO;
import org.ecomapp.userMS.services.sellerService.SellerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seller/profile")
@RequiredArgsConstructor
@Tag(name = "4. Seller Profile", description = "Seller profile management")
public class SellerController {
    private final SellerService sellerService;

    @Operation(summary = "Get seller profile - SELLER only")
    @GetMapping("/me")
    public ResponseEntity<SellerResponseDTO> getById() {
        return new ResponseEntity<>(sellerService.getSeller(), HttpStatus.OK);
    }

    @Operation(summary = "Create seller profile - SELLER only")
    @PostMapping("/me")
    public ResponseEntity<SellerResponseDTO> create(@Valid @RequestBody SellerRequestDTO sellerRequestDTO) {
        return new ResponseEntity<>(sellerService.createSeller(sellerRequestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Update seller profile - SELLER only")
    @PutMapping("/me")
    public ResponseEntity<SellerResponseDTO> update(@Valid @RequestBody SellerRequestDTO sellerRequestDTO) {
        return new ResponseEntity<>(sellerService.updateSeller(sellerRequestDTO), HttpStatus.OK);
    }

}
