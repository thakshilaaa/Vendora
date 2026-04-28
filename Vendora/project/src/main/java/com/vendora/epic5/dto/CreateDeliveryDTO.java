package com.vendora.epic5.dto;

import lombok.Data;

@Data
public class CreateDeliveryDTO {
    private Long orderId;
    private Long customerId;
    private String customerDistrict;
    private String trackingNumber;
    private String deliveryAddress;
    private String notes;
    private Long createdBy;
}
