package com.vendora.epic1.controller;

import com.vendora.common.ApiResponse;
import com.vendora.common.Constants;
import com.vendora.common.PaginationResponse;
import com.vendora.epic1.dto.user.UserDetailsResponse;
import com.vendora.epic1.dto.user.UserSummaryResponse;
import com.vendora.epic1.exception.ResourceNotFoundException;
import com.vendora.epic1.model.User;
import com.vendora.epic1.model.enums.RoleType;
import com.vendora.epic1.model.enums.UserStatus;
import com.vendora.epic1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping(Constants.ADMIN_BASE + "/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

        private final UserRepository userRepository;

        @GetMapping("/users")
        public ResponseEntity<ApiResponse<PaginationResponse<UserSummaryResponse>>> getAllUsers(
                        @RequestParam(defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
                        @RequestParam(defaultValue = Constants.DEFAULT_PAGE_SIZE) int size,
                        @RequestParam(defaultValue = Constants.DEFAULT_SORT_BY) String sortBy,
                        @RequestParam(defaultValue = Constants.DEFAULT_SORT_DIRECTION) String direction) {
                Sort sort = direction.equalsIgnoreCase("desc")
                                ? Sort.by(sortBy).descending()
                                : Sort.by(sortBy).ascending();

                Pageable pageable = PageRequest.of(page, size, sort);

                Page<UserSummaryResponse> userPage = userRepository.findAll(pageable)
                                .map(this::mapToSummaryResponse);

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                Constants.USER_LIST_RETRIEVED,
                                                PaginationResponse.from(userPage)));
        }

        @GetMapping("/users/{id}")
        public ResponseEntity<ApiResponse<UserDetailsResponse>> getUserById(@PathVariable Long id) {
                User user = userRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                Constants.USER_DETAILS_RETRIEVED,
                                                mapToDetailsResponse(user)));
        }

        @GetMapping("/users/role/{role}")
        public ResponseEntity<ApiResponse<List<UserSummaryResponse>>> getUsersByRole(@PathVariable RoleType role) {
                List<UserSummaryResponse> users = userRepository.findAll()
                                .stream()
                                .filter(u -> u.getRole() == role)
                                .map(this::mapToSummaryResponse)
                                .toList();

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                Constants.USER_LIST_RETRIEVED,
                                                users));
        }

        @GetMapping("/users/status/{status}")
        public ResponseEntity<ApiResponse<List<UserSummaryResponse>>> getUsersByStatus(
                        @PathVariable UserStatus status) {
                List<UserSummaryResponse> users = userRepository.findAll()
                                .stream()
                                .filter(u -> u.getStatus() == status)
                                .map(this::mapToSummaryResponse)
                                .toList();

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                Constants.USER_LIST_RETRIEVED,
                                                users));
        }

        private UserSummaryResponse mapToSummaryResponse(User user) {
                String fullName = user.getFullName();
                String[] nameParts = fullName != null ? fullName.trim().split("\\s+", 2) : new String[]{"", ""};
                String firstName = nameParts.length > 0 ? nameParts[0] : "";
                String lastName = nameParts.length > 1 ? nameParts[1] : "";

                return UserSummaryResponse.builder()
                                .id(user.getId())
                                .firstName(firstName)
                                .lastName(lastName)
                                .fullName(fullName)
                                .email(user.getEmail())
                                .phoneNumber(user.getPhone())
                                .role(user.getRole())
                                .status(user.getStatus())
                                .emailVerified(user.getEmailVerified())
                                .createdAt(user.getCreatedAt())
                                .updatedAt(user.getUpdatedAt())
                                .build();
        }

        private UserDetailsResponse mapToDetailsResponse(User user) {
                String fullName = user.getFullName();
                String[] nameParts = fullName != null ? fullName.trim().split("\\s+", 2) : new String[]{"", ""};
                String firstName = nameParts.length > 0 ? nameParts[0] : "";
                String lastName = nameParts.length > 1 ? nameParts[1] : "";

                return UserDetailsResponse.builder()
                                .id(user.getId())
                                .firstName(firstName)
                                .lastName(lastName)
                                .fullName(fullName)
                                .email(user.getEmail())
                                .phoneNumber(user.getPhone())
                                .role(user.getRole())
                                .status(user.getStatus())
                                .emailVerified(user.getEmailVerified())
                                .accountNonLocked(true)
                                .accountNonExpired(true)
                                .credentialsNonExpired(true)
                                .createdAt(user.getCreatedAt())
                                .updatedAt(user.getUpdatedAt())
                                .lastLoginAt(null)
                                .build();
        }
}
