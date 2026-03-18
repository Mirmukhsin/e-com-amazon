package org.ecomapp.userMS.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.ecomapp.userMS.dtos.response.UserResponseDTO;
import org.ecomapp.userMS.services.adminService.AdminService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Tag(name = "5. Admin", description = "Admin operations")
public class AdminController {
    private final AdminService adminService;

    @Operation(summary = "Get all users -ADMIN only")
    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(adminService.getAllUsers(page, size));
    }

    @Operation(summary = "Ban user -ADMIN only")
    @PatchMapping("/{userId}/ban")
    public ResponseEntity<Void> banUser(@PathVariable Long userId) {
        adminService.banUser(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Unban user -ADMIN only")
    @PatchMapping("/{userId}/unban")
    public ResponseEntity<Void> unbanUser(@PathVariable Long userId) {
        adminService.unBanUser(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
