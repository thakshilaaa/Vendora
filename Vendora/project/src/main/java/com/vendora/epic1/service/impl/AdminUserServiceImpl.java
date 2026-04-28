package com.vendora.epic1.service.impl;

import com.vendora.epic1.dto.admin.UpdateUserRoleRequest;
import com.vendora.epic1.dto.admin.UpdateUserStatusRequest;
import com.vendora.epic1.dto.auth.MessageResponse;
import com.vendora.epic1.dto.user.UserResponse;
import com.vendora.epic1.exception.ResourceNotFoundException;
import com.vendora.epic1.model.User;
import com.vendora.epic1.repository.UserRepository;
import com.vendora.epic1.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToUserResponse)
                .toList();
    }

    @Override
    public UserResponse getUserById(Long userId) {
        User user = findUserById(userId);
        return mapToUserResponse(user);
    }

    @Override
    @Transactional
    public MessageResponse updateUserRole(Long userId, UpdateUserRoleRequest request) {
        validateRoleRequest(request);

        User user = findUserById(userId);
        user.setRole(request.getRole());
        userRepository.save(user);

        return new MessageResponse("User role updated successfully");
    }

    @Override
    @Transactional
    public MessageResponse updateUserStatus(Long userId, UpdateUserStatusRequest request) {
        validateStatusRequest(request);

        User user = findUserById(userId);
        user.setStatus(request.getStatus());
        userRepository.save(user);

        return new MessageResponse("User status updated successfully");
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private void validateRoleRequest(UpdateUserRoleRequest request) {
        if (Objects.isNull(request) || Objects.isNull(request.getRole())) {
            throw new IllegalArgumentException("Role must not be null");
        }
    }

    private void validateStatusRequest(UpdateUserStatusRequest request) {
        if (Objects.isNull(request) || Objects.isNull(request.getStatus())) {
            throw new IllegalArgumentException("Status must not be null");
        }
    }

    private UserResponse mapToUserResponse(User user) {
        String firstName = user.getFirstName() == null ? "" : user.getFirstName().trim();
        String lastName = user.getLastName() == null ? "" : user.getLastName().trim();

        return UserResponse.builder()
                .id(user.getId())
                .fullName((firstName + " " + lastName).trim())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .status(user.getStatus())
                .emailVerified(Boolean.TRUE.equals(user.getIsEmailVerified()))
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}