package com.vendora.epic5.dto;

import lombok.Data;

@Data
public class AssignAgentDTO {
    private Long agentId;
    /** Service district of the agent — validated against customer's district before assignment */
    private String agentServiceDistrict;
    private Long assignedBy;
}
