package com.vendora.epic1.service;

import com.vendora.epic1.dto.auth.ChangePasswordRequest;
import com.vendora.epic1.dto.auth.ForgotPasswordRequest;
import com.vendora.epic1.dto.auth.MessageResponse;
import com.vendora.epic1.dto.auth.ResetPasswordRequest;
import com.vendora.epic1.dto.auth.SetPasswordRequest;

public interface PasswordService {

    MessageResponse forgotPassword(ForgotPasswordRequest request);

    MessageResponse resetPassword(ResetPasswordRequest request);

    MessageResponse setPasswordWithToken(SetPasswordRequest request);

    /** {@code principal} is the authenticated user's {@code userCode} (JWT subject). */
    MessageResponse changePassword(String principal, ChangePasswordRequest request);
}
