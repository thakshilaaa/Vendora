package com.vendora.statics.service;

import com.vendora.statics.dto.partnership.DeliveryPartnershipRequest;
import com.vendora.statics.dto.partnership.PartnershipApplicationResponse;
import com.vendora.statics.dto.partnership.SupplierPartnershipRequest;

public interface PartnershipService {

    PartnershipApplicationResponse applySupplierPartnership(SupplierPartnershipRequest request);

    PartnershipApplicationResponse applyDeliveryPartnership(DeliveryPartnershipRequest request);

    PartnershipApplicationResponse getApplicationById(Long id);
}
