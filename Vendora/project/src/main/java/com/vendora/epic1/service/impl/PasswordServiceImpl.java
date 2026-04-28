package com.vendora.epic1.service.impl;

import com.vendora.epic1.dto.auth.ChangePasswordRequest;
import com.vendora.epic1.dto.auth.ForgotPasswordRequest;
import com.vendora.epic1.dto.auth.MessageResponse;
import com.vendora.epic1.dto.auth.ResetPasswordRequest;
import com.vendora.epic1.dto.auth.SetPasswordRequest;
import com.vendora.epic1.exception.BadRequestException;
import com.vendora.epic1.exception.InvalidCredentialsException;
import com.vendora.epic1.exception.InvalidTokenException;
import com.vendora.epic1.model.PasswordResetToken;
import com.vendora.epic1.model.User;
import com.vendora.epic1.model.enums.UserStatus;
import com.vendora.epic1.repository.PasswordResetTokenRepository;
import com.vendora.epic1.repository.UserRepository;
import com.vendora.epic1.service.EmailService;
import com.vendora.epic1.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {

    private static final Logger log = LoggerFactory.getLogger(PasswordServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    @Transactional
    public MessageResponse forgotPassword(ForgotPasswordRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        List<User> withEmail = userRepository.findAllByEmailIgnoreCase(email);
        if (withEmail.isEmpty()) {
            return new MessageResponse("If an account exists for this email, instructions have been sent.");
        }
        User user;
        if (withEmail.size() == 1) {
            user = withEmail.get(0);
        } else {
            if (request.getPhone() == null || request.getPhone().isBlank()) {
                return new MessageResponse(
                        "This email is shared by several accounts. Enter the phone number you registered with, then resubmit.");
            }
            user = userRepository.findByEmailIgnoreCaseAndPhone(email, request.getPhone().trim())
                    .orElse(null);
            if (user == null) {
                return new MessageResponse("If an account exists for this email, instructions have been sent.");
            }
        }

        passwordResetTokenRepository.deleteByUser(user);
        PasswordResetToken token = new PasswordResetToken(
                UUID.randomUUID().toString(),
                user,
                Instant.now().plus(24, ChronoUnit.HOURS)
        );
        passwordResetTokenRepository.save(token);

        try {
            emailService.sendPasswordResetEmail(user.getEmail(), token.getToken());
        } catch (Exception e) {
            log.warn("Could not send password reset email: {}", e.getMessage());
        }

        return new MessageResponse("If an account exists for this email, instructions have been sent.");
    }

    @Override
    @Transactional
    public MessageResponse resetPassword(ResetPasswordRequest request) {
        PasswordResetToken token = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new InvalidTokenException("Invalid reset token"));

        if (token.getExpiryDate().isBefore(Instant.now())) {
            passwordResetTokenRepository.delete(token);
            throw new InvalidTokenException("Reset token has expired");
        }

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        if (user.getStatus() == UserStatus.INACTIVE || user.getStatus() == UserStatus.PENDING_VERIFICATION) {
            user.setStatus(UserStatus.ACTIVE);
        }
        userRepository.save(user);

        passwordResetTokenRepository.delete(token);

        return new MessageResponse("Password reset successfully");
    }

    @Override
    @Transactional
    public MessageResponse setPasswordWithToken(SetPasswordRequest request) {
        PasswordResetToken token = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired token"));

        if (token.getExpiryDate().isBefore(Instant.now())) {
            passwordResetTokenRepository.delete(token);
            throw new InvalidTokenException("Token has expired");
        }

        User user = token.getUser();

        // Email guard: if an email is supplied (partner complete-registration flow),
        // it must exactly match the approved email on file.
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String suppliedEmail = request.getEmail().trim().toLowerCase();
            if (!user.getEmail().equalsIgnoreCase(suppliedEmail)) {
                throw new BadRequestException(
                    "You must use the email address that was submitted in your partnership application.");
            }
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setStatus(UserStatus.ACTIVE);
        user.setIsEmailVerified(true);
        userRepository.save(user);
        passwordResetTokenRepository.delete(token);

        return new MessageResponse("Password set successfully. You can now sign in.");
    }

    @Override
    @Transactional
    public MessageResponse changePassword(String principal, ChangePasswordRequest request) {
        User user = userRepository.findByUserCodeIgnoreCase(principal)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return new MessageResponse("Password changed successfully");
    }
}
