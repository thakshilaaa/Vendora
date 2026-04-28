package com.vendora.statics.service.impl;

import com.vendora.epic1.model.User;
import com.vendora.epic1.repository.UserRepository;
import com.vendora.epic1.exception.ResourceNotFoundException;
import com.vendora.statics.dto.partnership.DeliveryPartnershipRequest;
import com.vendora.statics.dto.partnership.PartnershipApplicationResponse;
import com.vendora.statics.dto.partnership.SupplierPartnershipRequest;
import com.vendora.statics.model.PartnershipApplication;
import com.vendora.statics.model.enums.ApplicationStatus;
import com.vendora.statics.model.enums.ApplicationType;
import com.vendora.statics.repository.PartnershipApplicationRepository;
import com.vendora.statics.service.PartnershipService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PartnershipServiceImpl implements PartnershipService {

    private final PartnershipApplicationRepository partnershipApplicationRepository;
    private final UserRepository userRepository;

    public PartnershipServiceImpl(
            PartnershipApplicationRepository partnershipApplicationRepository,
            UserRepository userRepository
    ) {
        this.partnershipApplicationRepository = partnershipApplicationRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public PartnershipApplicationResponse applySupplierPartnership(SupplierPartnershipRequest request) {
        PartnershipApplication app = new PartnershipApplication();
        app.setApplicationType(ApplicationType.SUPPLIER_PARTNERSHIP);
        app.setBusinessName(request.getBusinessName());
        app.setContactPersonName(request.getContactPersonName());
        app.setEmail(request.getEmail().trim().toLowerCase());
        app.setPhoneNumber(request.getPhoneNumber());
        app.setBusinessAddress(request.getBusinessAddress());
        app.setBusinessRegistrationNumber(request.getBusinessRegistrationNumber());
        app.setProductCategory(request.getProductCategory());
        app.setDescription(request.getDescription());
        app.setUser(currentUserOrNull());
        app.setStatus(ApplicationStatus.PENDING);
        return toResponse(partnershipApplicationRepository.save(app));
    }

    @Override
    @Transactional
    public PartnershipApplicationResponse applyDeliveryPartnership(DeliveryPartnershipRequest request) {
        PartnershipApplication app = new PartnershipApplication();
        app.setApplicationType(ApplicationType.DELIVERY_PARTNERSHIP);
        app.setBusinessName(request.getCompanyName());
        app.setContactPersonName(request.getContactPersonName());
        app.setEmail(request.getEmail().trim().toLowerCase());
        app.setPhoneNumber(request.getPhoneNumber());
        app.setBusinessAddress(request.getBusinessAddress());
        app.setServiceAreas(request.getServiceAreas());
        app.setFleetSize(request.getFleetSize());
        app.setDescription(request.getDescription());
        app.setUser(currentUserOrNull());
        app.setStatus(ApplicationStatus.PENDING);
        return toResponse(partnershipApplicationRepository.save(app));
    }

    @Override
    @Transactional(readOnly = true)
    public PartnershipApplicationResponse getApplicationById(Long id) {
        PartnershipApplication app = partnershipApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partnership application not found"));
        return toResponse(app);
    }

    private User currentUserOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        return userRepository.findByUserCodeIgnoreCase(auth.getName()).orElse(null);
    }

    static PartnershipApplicationResponse toResponse(PartnershipApplication app) {
        return PartnershipApplicationResponse.builder()
                .id(app.getId())
                .applicationType(app.getApplicationType())
                .status(app.getStatus())
                .businessName(app.getBusinessName())
                .contactPersonName(app.getContactPersonName())
                .email(app.getEmail())
                .phoneNumber(app.getPhoneNumber())
                .businessAddress(app.getBusinessAddress())
                .businessRegistrationNumber(app.getBusinessRegistrationNumber())
                .serviceAreas(app.getServiceAreas())
                .fleetSize(app.getFleetSize())
                .productCategory(app.getProductCategory())
                .description(app.getDescription())
                .adminRemarks(app.getAdminRemarks())
                .submittedAt(app.getSubmittedAt())
                .reviewedAt(app.getReviewedAt())
                .createdAt(app.getSubmittedAt())
                .updatedAt(app.getReviewedAt() != null ? app.getReviewedAt() : app.getSubmittedAt())
                .build();
    }
}
