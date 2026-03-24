package org.ecomapp.userMS.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ecomapp.exceptionHandling.ErrorResponseDTO;
import org.ecomapp.securityMS.jwtConfig.LogoutRequestDTO;
import org.ecomapp.securityMS.jwtConfig.refreshToken.RefreshTokenRequestDTO;
import org.ecomapp.userMS.dtos.request.LoginRequestDTO;
import org.ecomapp.userMS.dtos.request.RegisterRequestDTO;
import org.ecomapp.userMS.dtos.response.LoginResponseDTO;
import org.ecomapp.userMS.dtos.response.UserResponseDTO;
import org.ecomapp.userMS.services.authService.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "1. Authentication", description = "Register and login endpoints")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Register new user")
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        return new ResponseEntity<>(authService.register(registerRequestDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Login")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login successful",
                    content = @Content(
                            schema = @Schema(
                                    implementation = LoginResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponseDTO.class))
            )
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        return new ResponseEntity<>(authService.login(loginRequestDTO), HttpStatus.OK);
    }

    @Operation(summary = "Refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refresh(@Valid @RequestBody RefreshTokenRequestDTO dto) {
        return new ResponseEntity<>(authService.refresh(dto), HttpStatus.OK);
    }

    @Operation(summary = "Logout")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequestDTO dto) {
        authService.logout(dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
