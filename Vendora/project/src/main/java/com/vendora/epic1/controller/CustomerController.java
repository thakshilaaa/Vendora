package com.vendora.epic1.controller;

import com.vendora.common.ApiResponse;
import com.vendora.common.Constants;
import com.vendora.epic1.dto.user.ProfileResponse;
import com.vendora.epic1.dto.user.UpdateProfileRequest;
import com.vendora.epic1.dto.user.UserDetailsResponse;
import com.vendora.epic1.model.User;
import com.vendora.epic1.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController("epic1CustomerController")
@RequestMapping(Constants.CUSTOMER_BASE)
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerController {

    private final UserService userService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<String>> getDashboard() {
        User user = userService.getCurrentUser();
        String welcomeMessage = "Welcome to your dashboard, " + user.getFullName() + "!";
        return ResponseEntity.ok(ApiResponse.success("Dashboard loaded", welcomeMessage));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfile() {
        ProfileResponse response = userService.getCurrentUserProfile();
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved", response));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<ProfileResponse>> updateProfile(
            @RequestBody UpdateProfileRequest request) {
        ProfileResponse response = userService.updateProfile(request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated", response));
    }

    @PutMapping("/settings")
    public ResponseEntity<ApiResponse<String>> updateSettings(
            Authentication authentication,
            @RequestBody UserDetailsResponse settings) {
        // Implement settings update logic
        return ResponseEntity.ok(ApiResponse.success("Settings updated", "Settings saved successfully"));
    }
}