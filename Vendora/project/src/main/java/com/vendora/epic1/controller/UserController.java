package com.vendora.epic1.controller;

import com.vendora.epic1.dto.auth.MessageResponse;
import com.vendora.epic1.dto.user.*;
import com.vendora.epic1.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getProfile() {
        return ResponseEntity.ok(userService.getCurrentUserProfile());
    }

    @PutMapping("/me")
    public ResponseEntity<ProfileResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(request));
    }

    @DeleteMapping("/me")
    public ResponseEntity<MessageResponse> deleteAccount(@Valid @RequestBody DeleteAccountRequest request) {
        return ResponseEntity.ok(userService.deleteAccount(request));
    }

    @DeleteMapping("/me/permanent")
    public ResponseEntity<MessageResponse> permanentDelete(@Valid @RequestBody PermanentDeleteAccountRequest request) {
        return ResponseEntity.ok(userService.permanentDeleteAccount(request));
    }
}