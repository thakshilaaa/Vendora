package com.vendora.epic3.dto;

import com.vendora.epic3.model.LocationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CheckoutRequest {
    private List<Long> itemIds;
    @NotNull(message = "Choose a delivery area")
    private LocationType locationType;
    @NotBlank(message = "Delivery address is required")
    private String address;
    private String paymentMethod;

    public List<Long> getItemIds() { return itemIds; }
    public void setItemIds(List<Long> itemIds) { this.itemIds = itemIds; }
    public LocationType getLocationType() { return locationType; }
    public void setLocationType(LocationType locationType) { this.locationType = locationType; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}
