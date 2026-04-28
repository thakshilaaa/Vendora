package com.vendora.epic1.service;

import com.vendora.epic1.dto.auth.AuthResponse;
import com.vendora.epic1.dto.auth.MessageResponse;
import com.vendora.epic1.dto.partnership.PartnershipApplicationRequest;
import com.vendora.epic1.dto.partnership.PartnershipApplicationResponse;
import com.vendora.epic1.dto.partnership.RejectPartnerRequest;

import java.util.List;

public interface PartnershipService {
    AuthResponse submitApplication(PartnershipApplicationRequest request);
    List<PartnershipApplicationResponse> getPendingApplications();
    MessageResponse approveApplication(Long id);
    MessageResponse rejectApplication(RejectPartnerRequest request);
}
