package com.vendora.epic1.service.impl;

import com.vendora.epic1.dto.auth.AuthResponse;
import com.vendora.epic1.dto.auth.MessageResponse;
import com.vendora.epic1.dto.partnership.PartnershipApplicationRequest;
import com.vendora.epic1.dto.partnership.PartnershipApplicationResponse;
import com.vendora.epic1.dto.partnership.RejectPartnerRequest;
import com.vendora.epic1.exception.BadRequestException;
import com.vendora.epic1.exception.DuplicateResourceException;
import com.vendora.epic1.exception.ResourceNotFoundException;
import com.vendora.epic1.model.PartnershipApplication;
import com.vendora.epic1.model.PasswordResetToken;
import com.vendora.epic1.model.User;
import com.vendora.epic1.model.enums.ApplicantType;
import com.vendora.epic1.model.enums.PartnershipStatus;
import com.vendora.epic1.model.enums.RoleType;
import com.vendora.epic1.model.enums.UserStatus;
import com.vendora.epic1.repository.PartnershipApplicationRepository;
import com.vendora.epic1.repository.PasswordResetTokenRepository;
import com.vendora.epic1.repository.UserRepository;
import com.vendora.epic1.service.EmailService;
import com.vendora.epic1.service.PartnershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service("epic1PartnershipServiceImpl")
@RequiredArgsConstructor
public class PartnershipServiceImpl implements PartnershipService {

    private final PartnershipApplicationRepository partnershipApplicationRepository;
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.base-url:http://localhost:8080}")
    private String appBaseUrl;

    @Override
    @Transactional
    public AuthResponse submitApplication(PartnershipApplicationRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new BadRequestException("Email is required.");
        }
        if (request.getFullName() == null || request.getFullName().isBlank()) {
            throw new BadRequestException("Full name is required.");
        }
        if (request.getApplicantType() == null || request.getApplicantType().isBlank()) {
            throw new BadRequestException("Applicant type is required.");
        }

        String email = request.getEmail().trim().toLowerCase();

        if (partnershipApplicationRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("An application with this email already exists.");
        }

        final ApplicantType applicantType;
        try {
            applicantType = ApplicantType.valueOf(request.getApplicantType().trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid applicant type. Use SUPPLIER or DELIVERY.");
        }

        PartnershipApplication application = new PartnershipApplication();
        application.setApplicantType(applicantType);
        application.setFullName(request.getFullName().trim());
        application.setContactPersonName(request.getFullName().trim());
        application.setEmail(email);
        application.setPhone(request.getPhone());
        application.setNic(request.getNic());
        application.setAddressLine1(request.getAddressLine1());
        application.setBusinessAddress(request.getAddressLine1());
        application.setProvince(request.getProvince());
        application.setDistrict(request.getDistrict());
        application.setCity(request.getCity());
        application.setPostalCode(request.getPostalCode());
        
        application.setBusinessName(request.getBusinessName());
        application.setBusinessRegNumber(request.getBusinessRegNumber());
        application.setTinNumber(request.getTinNumber());
        application.setProductCategory(request.getProductCategory());
        application.setProductDetails(request.getProductDetails());
        
        application.setStatus(PartnershipStatus.PENDING);

        partnershipApplicationRepository.save(application);

        try {
            emailService.sendPartnershipAcknowledgementEmail(application.getEmail(), application.getFullName());
        } catch (Exception e) {
            // Log and continue, do not block the application submission
        }

        AuthResponse response = new AuthResponse();
        response.setSuccess(true);
        response.setMessage("Application submitted successfully. We will review it within 3 business days.");
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PartnershipApplicationResponse> getPendingApplications() {
        return partnershipApplicationRepository.findByStatus(PartnershipStatus.PENDING).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public MessageResponse approveApplication(Long id) {
        PartnershipApplication application = partnershipApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (application.getStatus() != PartnershipStatus.PENDING) {
            throw new BadRequestException("Application is not in PENDING state");
        }

        // Update application status
        application.setStatus(PartnershipStatus.APPROVED);
        application.setReviewedAt(LocalDateTime.now());
        partnershipApplicationRepository.save(application);

        // Create partner user if absent, or refresh the existing pending account (match email + phone when possible).
        String appEmail = application.getEmail().trim().toLowerCase();
        String appPhone = application.getPhone() != null ? application.getPhone().trim() : null;
        User user;
        if (appPhone != null && !appPhone.isBlank()) {
            user = userRepository.findByEmailIgnoreCaseAndPhone(appEmail, appPhone).orElseGet(User::new);
        } else {
            user = userRepository.findAllByEmailIgnoreCase(appEmail).stream().findFirst().orElseGet(User::new);
        }
        user.setFullName(application.getFullName());
        user.setEmail(appEmail);
        user.setPasswordHash(passwordEncoder.encode(UUID.randomUUID().toString()));
        user.setRole(application.getApplicantType() == ApplicantType.SUPPLIER ? RoleType.ROLE_SUPPLIER : RoleType.ROLE_DELIVERY);
        user.setStatus(UserStatus.INACTIVE);
        user.setEmailVerified(false);
        user.setPhone(application.getPhone());
        userRepository.save(user);

        // Generate token for password setup
        passwordResetTokenRepository.deleteByUser(user);
        PasswordResetToken token = new PasswordResetToken(
                UUID.randomUUID().toString(),
                user,
                Instant.now().plus(72, ChronoUnit.HOURS)
        );
        passwordResetTokenRepository.save(token);

        // Send approval email with URL
        try {
            String encodedEmail = URLEncoder.encode(application.getEmail(), StandardCharsets.UTF_8.toString());
            String completeRegistrationUrl = appBaseUrl + "/complete-registration?token=" + token.getToken() + "&email=" + encodedEmail;
            emailService.sendPartnershipApprovalEmail(application.getEmail(), completeRegistrationUrl);
        } catch (Exception e) {
            // Log error
        }

        return new MessageResponse("Application approved and email sent to the partner.");
    }

    @Override
    @Transactional
    public MessageResponse rejectApplication(RejectPartnerRequest request) {
        PartnershipApplication application = partnershipApplicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (application.getStatus() != PartnershipStatus.PENDING) {
            throw new BadRequestException("Application is not in PENDING state");
        }

        application.setStatus(PartnershipStatus.REJECTED);
        application.setReviewNote(request.getNote());
        application.setReviewedAt(LocalDateTime.now());
        partnershipApplicationRepository.save(application);

        try {
            emailService.sendPartnershipRejectionEmail(application.getEmail(), application.getFullName(), request.getNote());
        } catch (Exception e) {
            // Log error
        }

        return new MessageResponse("Application rejected and email sent.");
    }

    private PartnershipApplicationResponse mapToResponse(PartnershipApplication app) {
        PartnershipApplicationResponse res = new PartnershipApplicationResponse();
        res.setId(app.getId());
        res.setApplicantType(app.getApplicantType());
        res.setFullName(app.getFullName());
        res.setEmail(app.getEmail());
        res.setPhone(app.getPhone());
        res.setCity(app.getCity());
        res.setDistrict(app.getDistrict());
        res.setBusinessName(app.getBusinessName());
        res.setStatus(app.getStatus());
        res.setCreatedAt(app.getCreatedAt());
        res.setReviewNote(app.getReviewNote());
        return res;
    }
}
