package com.vendora.epic1.service;

import com.vendora.epic1.dto.auth.MessageResponse;
import com.vendora.epic1.dto.auth.VerifyEmailRequest;

public interface EmailVerificationService {

    MessageResponse verifyEmail(VerifyEmailRequest request);
}
