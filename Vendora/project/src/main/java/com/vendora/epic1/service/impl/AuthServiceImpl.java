package com.vendora.epic1.service.impl;

import com.vendora.epic1.dto.auth.AdminRegisterRequest;
import com.vendora.epic1.dto.auth.AuthResponse;
import com.vendora.epic1.dto.auth.CustomerRegisterRequest;
import com.vendora.epic1.dto.auth.ForgotPasswordRequest;
import com.vendora.epic1.dto.auth.LoginRequest;
import com.vendora.epic1.dto.auth.LoginResponse;
import com.vendora.epic1.dto.auth.MessageResponse;
import com.vendora.epic1.dto.auth.ResendVerificationRequest;
import com.vendora.epic1.dto.auth.ResetPasswordRequest;
import com.vendora.epic1.dto.auth.SetPasswordRequest;
import com.vendora.epic1.dto.auth.VerifyEmailRequest;
import com.vendora.epic1.exception.AccountDisabledException;
import com.vendora.epic1.exception.AccountNotVerifiedException;
import com.vendora.epic1.exception.BadRequestException;
import com.vendora.epic1.exception.InvalidCredentialsException;
import com.vendora.epic1.model.EmailVerificationToken;
import com.vendora.epic1.model.User;
import com.vendora.epic1.model.enums.District;
import com.vendora.epic1.model.enums.Province;
import com.vendora.epic1.model.enums.RoleType;
import com.vendora.epic1.model.enums.UserStatus;
import com.vendora.epic1.repository.EmailVerificationTokenRepository;
import com.vendora.epic1.repository.UserRepository;
import com.vendora.epic1.security.JwtService;
import com.vendora.epic1.service.AuthService;
import com.vendora.epic1.service.EmailService;
import com.vendora.epic1.service.EmailVerificationService;
import com.vendora.epic1.service.PasswordService;
import com.vendora.epic1.model.enums.VerificationMethod;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    private static final int VERIFICATION_OTP_MINUTES = 5;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int MAX_FAILED_LOGIN_ATTEMPTS = 5;
    private static final long ACCOUNT_LOCK_DURATION_MINUTES = 30;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final EmailService emailService;
    private final EmailVerificationService emailVerificationService;
    private final PasswordService passwordService;
    private final com.vendora.epic1.service.SystemSetupService systemSetupService;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager, JwtService jwtService,
                           EmailVerificationTokenRepository emailVerificationTokenRepository,
                           EmailService emailService,
                           EmailVerificationService emailVerificationService,
                           PasswordService passwordService,
                           com.vendora.epic1.service.SystemSetupService systemSetupService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.emailService = emailService;
        this.emailVerificationService = emailVerificationService;
        this.passwordService = passwordService;
        this.systemSetupService = systemSetupService;
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = resolveUserForLogin(request);

        // Check if account is locked
        if (user.getAccountLockedUntil() != null
                && user.getAccountLockedUntil().isAfter(java.time.LocalDateTime.now())) {
            throw new AccountDisabledException(
                    "Account is temporarily locked due to too many failed login attempts. Try again later.");
        }

        String authPrincipal = user.getUserCode();
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authPrincipal, request.getPassword()));

            if (!authentication.isAuthenticated()) {
                throw new InvalidCredentialsException("Invalid email or password");
            }
        } catch (BadCredentialsException e) {
            // Increment failed attempts
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            if (user.getFailedLoginAttempts() >= MAX_FAILED_LOGIN_ATTEMPTS) {
                user.setAccountLockedUntil(java.time.LocalDateTime.now().plusMinutes(ACCOUNT_LOCK_DURATION_MINUTES));
                user.setStatus(UserStatus.SUSPENDED);
                log.warn("Account locked for user {} due to too many failed attempts", authPrincipal);
            }
            userRepository.save(user);
            throw new InvalidCredentialsException("Invalid email or password");
        }

        // Reset failed attempts on successful login
        user.setFailedLoginAttempts(0);
        user.setAccountLockedUntil(null);
        userRepository.save(user);

        validateLoginEligibility(user);

        // Audit logging
        log.info("Successful login for user: {} ({})", user.getUserCode(), user.getEmail());

        LoginResponse response = new LoginResponse();
        response.setAccessToken(jwtService.generateToken(user.getUserCode()));
        response.setTokenType("Bearer");
        response.setRefreshToken(null);
        response.setUserId(user.getId());
        response.setUserCode(user.getUserCode());
        response.setRole(user.getRole());
        response.setDashboardPath(resolveDashboardPath(user.getRole()));
        return response;
    }

    private String resolveDashboardPath(RoleType role) {
        return switch (role) {
            case ROLE_ADMIN -> "/admin-dashboard";
            case ROLE_SUPPLIER -> "/supplier-dashboard";
            case ROLE_DELIVERY -> "/delivery-dashboard";
            default -> "/customer-dashboard";
        };
    }

    @Override
    @Transactional
    public AuthResponse registerCustomer(CustomerRegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        User user = new User();
        user.setFullName(request.getFullName().trim());
        user.setEmail(email);
        user.setPhone(request.getPhone().trim());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(RoleType.ROLE_CUSTOMER);
        user.setStatus(UserStatus.PENDING_VERIFICATION);
        user.setEmailVerified(false);
        user.setVerificationMethod(VerificationMethod.EMAIL);
        user.setNic(request.getNic().trim());
        user.setAddressLine1(request.getAddressLine1().trim());
        if (request.getAddressLine2() != null && !request.getAddressLine2().isBlank()) {
            user.setAddressLine2(request.getAddressLine2().trim());
        }
        user.setCity(request.getCity().trim());
        user.setDistrict(request.getDistrict());
        user.setProvince(request.getProvince());
        if (request.getPostalCode() != null && !request.getPostalCode().isBlank()) {
            user.setPostalCode(request.getPostalCode().trim());
        }

        userRepository.save(user);

        createAndSendVerificationToken(user);

        AuthResponse response = new AuthResponse();
        response.setSuccess(true);
        response.setMessage("Registration successful. Check your email to verify your account.");
        response.setUserCode(user.getUserCode());
        return response;
    }

    @Override
    @Transactional
    public AuthResponse registerAdmin(AdminRegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        User user = new User();
        user.setFullName(request.getUsername().trim());
        user.setEmail(email);
        user.setPhone(request.getPhone().trim());
        user.setNic(request.getNic().trim());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(RoleType.ROLE_ADMIN);
        user.setStatus(UserStatus.PENDING_VERIFICATION);
        user.setEmailVerified(false);
        user.setVerificationMethod(VerificationMethod.EMAIL);
        user.setAddressLine1("N/A");
        user.setCity("Colombo");
        user.setDistrict(District.COLOMBO);
        user.setProvince(Province.WESTERN);

        userRepository.save(user);

        createAndSendVerificationToken(user);

        // First admin launch unlock
        systemSetupService.launchSite();

        AuthResponse response = new AuthResponse();
        response.setSuccess(true);
        response.setMessage("Admin registered successfully. Please verify your account. The platform is now unlocked.");
        response.setUserCode(user.getUserCode());
        return response;
    }

    @Override
    public MessageResponse forgotPassword(ForgotPasswordRequest request) {
        return passwordService.forgotPassword(request);
    }

    @Override
    public MessageResponse resetPassword(ResetPasswordRequest request) {
        return passwordService.resetPassword(request);
    }

    @Override
    public MessageResponse verifyEmail(VerifyEmailRequest request) {
        return emailVerificationService.verifyEmail(request);
    }

    @Override
    @Transactional
    public MessageResponse resendVerification(ResendVerificationRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        User user = resolveUserForResendOrForgotEmail(email, request.getPhone());

        if (Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new BadRequestException("Email is already verified");
        }

        createAndSendVerificationToken(user);

        return new MessageResponse("Verification email has been sent.");
    }

    @Override
    public MessageResponse setPasswordWithToken(SetPasswordRequest request) {
        return passwordService.setPasswordWithToken(request);
    }

    /**
     * User codes are generated as U + 7 hex characters (see User @PrePersist).
     */
    private static boolean looksLikeUserCode(String raw) {
        if (raw == null || raw.length() != 8) {
            return false;
        }
        char first = raw.charAt(0);
        if (first != 'U' && first != 'u') {
            return false;
        }
        for (int i = 1; i < 8; i++) {
            char c = raw.charAt(i);
            if ((c < '0' || c > '9') && (c < 'A' || c > 'F') && (c < 'a' || c > 'f')) {
                return false;
            }
        }
        return true;
    }

    private User resolveUserForLogin(LoginRequest request) {
        String raw = request.getUsernameOrEmail().trim();
        if (looksLikeUserCode(raw)) {
            return userRepository.findByUserCodeIgnoreCase(raw)
                    .orElseThrow(() -> new InvalidCredentialsException("Invalid user ID or password"));
        }
        if (!raw.contains("@")) {
            throw new InvalidCredentialsException("Invalid email, user ID, or password");
        }
        String email = raw.toLowerCase();
        List<User> users = userRepository.findAllByEmailIgnoreCase(email);
        if (users.isEmpty()) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
        if (users.size() == 1) {
            return users.get(0);
        }
        String phone = request.getPhone();
        if (phone == null || phone.isBlank()) {
            throw new BadRequestException(
                    "This email is shared by several accounts. Sign in with your User ID (in your welcome email) "
                            + "or add your phone number in the sign-in form.");
        }
        return userRepository.findByEmailIgnoreCaseAndPhone(email, phone.trim())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email, phone, or password"));
    }

    private User resolveUserForResendOrForgotEmail(String email, String phone) {
        List<User> users = userRepository.findAllByEmailIgnoreCase(email);
        if (users.isEmpty()) {
            throw new BadRequestException("No account found for this email");
        }
        if (users.size() == 1) {
            return users.get(0);
        }
        if (phone == null || phone.isBlank()) {
            throw new BadRequestException(
                    "Multiple accounts use this email. Enter the phone number you registered with.");
        }
        return userRepository.findByEmailIgnoreCaseAndPhone(email, phone.trim())
                .orElseThrow(() -> new BadRequestException("No account matches this email and phone number."));
    }

    private void validateLoginEligibility(User user) {
        if (user.getStatus() == UserStatus.DELETED) {
            throw new AccountDisabledException("This account cannot sign in.");
        }

        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new AccountDisabledException("Account is suspended due to security reasons.");
        }

        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new AccountDisabledException("Account is inactive. Complete setup or contact support.");
        }

        if (!Boolean.TRUE.equals(user.getIsEmailVerified())) {
            throw new AccountNotVerifiedException("Please verify your email before signing in.");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new AccountDisabledException("Account is not active.");
        }
    }

    private void createAndSendVerificationToken(User user) {
        emailVerificationTokenRepository.deleteByUser(user);

        byte[] random = new byte[32];
        SECURE_RANDOM.nextBytes(random);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(random);
        EmailVerificationToken token = new EmailVerificationToken(
                rawToken,
                user,
                Instant.now().plus(VERIFICATION_OTP_MINUTES, ChronoUnit.MINUTES));

        emailVerificationTokenRepository.save(token);

        try {
            emailService.sendVerificationEmail(user.getEmail(), rawToken);
            log.info("Verification email sent to {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send verification email to {}: {}", user.getEmail(), e.getMessage(), e);
            log.error("FALLBACK for development only — store this token in DB is already done; use verify API with token or fix mail config. userId context: {}", user.getId());
        }
    }
}