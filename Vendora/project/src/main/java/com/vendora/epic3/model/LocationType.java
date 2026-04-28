package com.vendora.epic3.model;

public enum LocationType {
    CITY("City (1-2 days)", 300),
    SUBURB("Suburb (2-3 days)", 500),
    RURAL("Rural (4-5 days)", 800);

    private final String label;
    private final int fee;

    LocationType(String label, int fee) {
        this.label = label;
        this.fee = fee;
    }

    public String getLabel() { return label; }
    public int getFee() { return fee; }
}
