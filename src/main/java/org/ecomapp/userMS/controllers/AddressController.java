package org.ecomapp.userMS.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ecomapp.userMS.dtos.request.AddressRequestDTO;
import org.ecomapp.userMS.dtos.response.AddressResponseDTO;
import org.ecomapp.userMS.services.addressService.AddressService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/me/address")
@RequiredArgsConstructor
@Tag(name = "Addresses", description = "User address management")
public class AddressController {
    private final AddressService addressService;

    @Operation(summary = "Get all user addresses")
    @GetMapping
    public ResponseEntity<List<AddressResponseDTO>> getAll() {
        return new ResponseEntity<>(addressService.getUserAddresses(), HttpStatus.OK);
    }

    @Operation(summary = "Create user address")
    @PostMapping
    public ResponseEntity<AddressResponseDTO> create(@Valid @RequestBody AddressRequestDTO addressRequestDTO) {
        return new ResponseEntity<>(addressService.createAddress(addressRequestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Update user address")
    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponseDTO> update(@PathVariable Long addressId, @Valid @RequestBody AddressRequestDTO addressRequestDTO) {
        return new ResponseEntity<>(addressService.updateAddress(addressId, addressRequestDTO), HttpStatus.OK);
    }

    @Operation(summary = "Set default user address")
    @PatchMapping("/{addressId}/default")
    public ResponseEntity<Void> setDefault(@PathVariable Long addressId) {
        addressService.setDefaultAddress(addressId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Delete user address")
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> delete(@PathVariable Long addressId) {
        addressService.deleteAddress(addressId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
