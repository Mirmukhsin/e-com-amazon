package org.ecomapp.userservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ecomapp.userservice.dtos.request.CreateUserProfileDTO;
import org.ecomapp.userservice.dtos.request.UpdateProfileRequestDTO;
import org.ecomapp.userservice.dtos.response.UserResponseDTO;
import org.ecomapp.userservice.services.userService.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/profile")
@RequiredArgsConstructor
@Tag(name = "1. User profile", description = "User profile management")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Get user profile")
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getById(@RequestHeader("X-User-Id") Long userId) {
        return new ResponseEntity<>(userService.getUserById(userId), HttpStatus.OK);
    }


    @Operation(summary = "Update user profile")
    @PatchMapping("/me")
    public ResponseEntity<UserResponseDTO> update(@RequestHeader("X-User-Id") Long userId, @Valid @RequestBody UpdateProfileRequestDTO updateDTO) {
        return new ResponseEntity<>(userService.updateProfile(userId, updateDTO), HttpStatus.OK);
    }

//  TODO:  auth service APIs

    @Operation(summary = "Disable user account - ADMIN only - auth service")
    @DeleteMapping("/me")
    public ResponseEntity<Void> disableAccount(@RequestHeader("X-User-Id") Long userId) {
        userService.disableAccount(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Create user profile - from auth service")
    @PostMapping("/create")
    public ResponseEntity<UserResponseDTO> create(@RequestBody CreateUserProfileDTO createUserProfileDTO) {
        return new ResponseEntity<>(userService.createUserProfile(createUserProfileDTO), HttpStatus.CREATED);
    }
}
