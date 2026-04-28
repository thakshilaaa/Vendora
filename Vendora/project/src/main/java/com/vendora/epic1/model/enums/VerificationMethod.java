package com.vendora.epic1.model.enums;

public enum VerificationMethod {
    EMAIL,
    /** @deprecated Kept for legacy database rows; verification is email-only. */
    @Deprecated
    SMS
}
