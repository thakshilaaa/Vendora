package com.vendora.epic1.service.impl;

import com.vendora.epic1.dto.auth.MessageResponse;
import com.vendora.epic1.dto.user.DeleteAccountRequest;
import com.vendora.epic1.dto.user.PermanentDeleteAccountRequest;
import com.vendora.epic1.dto.user.ProfileResponse;
import com.vendora.epic1.dto.user.UpdateProfileRequest;
import com.vendora.epic1.exception.BadRequestException;
import com.vendora.epic1.exception.InvalidCredentialsException;
import com.vendora.epic1.exception.ResourceNotFoundException;
import com.vendora.epic1.exception.UnauthorizedException;
import com.vendora.epic1.model.User;
import com.vendora.epic1.model.enums.UserStatus;
import com.vendora.epic1.repository.UserRepository;
import com.vendora.epic1.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vendora.epic1.dto.user.UserDetailsResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        String e = email.trim().toLowerCase();
        List<User> list = userRepository.findAllByEmailIgnoreCase(e);
        if (list.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        if (list.size() > 1) {
            throw new BadRequestException(
                    "Several accounts use this email. Use the unique User ID (U…) from the user profile, not the email alone.");
        }
        return list.get(0);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetailsResponse getUserDetails(String email) {
        User user = getUserByEmail(email);
        return UserDetailsResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .status(user.getStatus())
                .emailVerified(user.getIsEmailVerified())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public UserDetailsResponse updateUserProfile(String email, UserDetailsResponse request) {
        User user = getUserByEmail(email);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        userRepository.save(user);
        return getUserDetails(email);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getCurrentUserProfile() {
        return mapToProfile(getCurrentUser());
    }

    @Override
    @Transactional
    public ProfileResponse updateProfile(UpdateProfileRequest request) {
        User user = getCurrentUser();
        String full = request.getFullName().trim();
        int space = full.indexOf(' ');
        if (space > 0) {
            user.setFirstName(full.substring(0, space).trim());
            user.setLastName(full.substring(space + 1).trim());
        } else {
            user.setFirstName(full);
            user.setLastName("");
        }
        user.setPhoneNumber(request.getPhoneNumber().trim());
        userRepository.save(user);
        return mapToProfile(user);
    }

    @Override
    @Transactional
    public MessageResponse deleteAccount(DeleteAccountRequest request) {
        User user = getCurrentUser();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Incorrect password");
        }
        user.setStatus(UserStatus.DELETED);
        userRepository.save(user);
        return new MessageResponse("Your account has been deactivated.");
    }

    @Override
    @Transactional
    public MessageResponse permanentDeleteAccount(PermanentDeleteAccountRequest request) {
        User user = getCurrentUser();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Incorrect password");
        }
        if (!"DELETE".equalsIgnoreCase(request.getConfirmation().trim())) {
            throw new BadRequestException("Confirmation must be: DELETE");
        }
        userRepository.delete(user);
        return new MessageResponse("Your account has been permanently deleted.");
    }

    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new UnauthorizedException("Not authenticated");
        }
        return userRepository.findByUserCodeIgnoreCase(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private static ProfileResponse mapToProfile(User user) {
        String first = user.getFirstName() == null ? "" : user.getFirstName();
        String last = user.getLastName() == null ? "" : user.getLastName();
        return ProfileResponse.builder()
                .id(user.getId())
                .fullName((first + " " + last).trim())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .status(user.getStatus())
                .emailVerified(user.getIsEmailVerified())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
