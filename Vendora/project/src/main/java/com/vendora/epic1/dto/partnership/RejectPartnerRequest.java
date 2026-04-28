package com.vendora.epic1.dto.partnership;

public class RejectPartnerRequest {
    private Long applicationId;
    private String note;

    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
