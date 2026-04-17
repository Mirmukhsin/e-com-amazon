package org.ecomapp.userservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ecomapp.userservice.dtos.request.AddressRequestDTO;
import org.ecomapp.userservice.dtos.response.AddressResponseDTO;
import org.ecomapp.userservice.services.addressService.AddressService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/addresses")
@RequiredArgsConstructor
@Tag(name = "3. Addresses", description = "User address management")
public class AddressController {
    private final AddressService addressService;

    @GetMapping("/me/{addressId}")
    public ResponseEntity<AddressResponseDTO> getById(@RequestHeader("X-User-Id") Long userId, @PathVariable Long addressId) {
        return new ResponseEntity<>(addressService.getById(userId, addressId), HttpStatus.OK);
    }

    @Operation(summary = "Get all user addresses")
    @GetMapping("/me")
    public ResponseEntity<List<AddressResponseDTO>> getAll(@RequestHeader("X-User-Id") Long userId) {
        return new ResponseEntity<>(addressService.getUserAddresses(userId), HttpStatus.OK);
    }

    @Operation(summary = "Create user address")
    @PostMapping("/me")
    public ResponseEntity<AddressResponseDTO> create(@RequestHeader("X-User-Id") Long userId, @Valid @RequestBody AddressRequestDTO addressRequestDTO) {
        return new ResponseEntity<>(addressService.createAddress(userId, addressRequestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Update user address")
    @PutMapping("/me/{addressId}")
    public ResponseEntity<AddressResponseDTO> update(@RequestHeader("X-User-Id") Long userId, @PathVariable Long addressId, @Valid @RequestBody AddressRequestDTO addressRequestDTO) {
        return new ResponseEntity<>(addressService.updateAddress(userId, addressId, addressRequestDTO), HttpStatus.OK);
    }

    @Operation(summary = "Set default user address")
    @PatchMapping("/me/{addressId}/default")
    public ResponseEntity<Void> setDefault(@RequestHeader("X-User-Id") Long userId, @PathVariable Long addressId) {
        addressService.setDefaultAddress(userId, addressId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Delete user address")
    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<Void> delete(@RequestHeader("X-User-Id") Long userId, @PathVariable Long addressId) {
        addressService.deleteAddress(userId, addressId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
