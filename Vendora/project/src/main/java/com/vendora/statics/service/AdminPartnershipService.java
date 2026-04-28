package com.vendora.statics.service;

import com.vendora.statics.dto.partnership.PartnershipApplicationResponse;
import com.vendora.statics.dto.partnership.ReviewPartnershipRequest;

import java.util.List;

public interface AdminPartnershipService {

    List<PartnershipApplicationResponse> getAllApplications();

    PartnershipApplicationResponse getApplicationById(Long applicationId);

    PartnershipApplicationResponse reviewApplication(Long applicationId, ReviewPartnershipRequest request);
}
