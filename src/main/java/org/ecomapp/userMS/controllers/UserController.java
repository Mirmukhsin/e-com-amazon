package org.ecomapp.userMS.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ecomapp.userMS.dtos.request.ChangePasswordRequestDTO;
import org.ecomapp.userMS.dtos.request.UpdateProfileRequestDTO;
import org.ecomapp.userMS.dtos.response.UserResponseDTO;
import org.ecomapp.userMS.services.userService.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/profile")
@RequiredArgsConstructor
@Tag(name = "User profile", description = "User profile management")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Get user profile")
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getById() {
        return new ResponseEntity<>(userService.getUserById(), HttpStatus.OK);
    }

    @Operation(summary = "Update user profile")
    @PatchMapping("/me")
    public ResponseEntity<UserResponseDTO> update(@Valid @RequestBody UpdateProfileRequestDTO updateDTO) {
        return new ResponseEntity<>(userService.updateProfile(updateDTO), HttpStatus.OK);
    }

    @Operation(summary = "Change user password")
    @PutMapping("/me/pswd")
    public ResponseEntity<Void> changePSWD(@Valid @RequestBody ChangePasswordRequestDTO changePswdDTO) {
        userService.changePassword(changePswdDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Delete user - ADMIN only")
    @DeleteMapping("/me")
    public ResponseEntity<Void> delete() {
        userService.deleteAccount();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
