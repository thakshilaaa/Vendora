package com.vendora.epic1.service;

import com.vendora.epic1.dto.auth.AdminRegisterRequest;
import com.vendora.epic1.dto.auth.AuthResponse;
import com.vendora.epic1.dto.auth.CustomerRegisterRequest;
import com.vendora.epic1.dto.auth.ForgotPasswordRequest;
import com.vendora.epic1.dto.auth.LoginRequest;
import com.vendora.epic1.dto.auth.LoginResponse;
import com.vendora.epic1.dto.auth.MessageResponse;
import com.vendora.epic1.dto.auth.ResendVerificationRequest;
import com.vendora.epic1.dto.auth.ResetPasswordRequest;
import com.vendora.epic1.dto.auth.SetPasswordRequest;
import com.vendora.epic1.dto.auth.VerifyEmailRequest;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    AuthResponse registerCustomer(CustomerRegisterRequest request);

    AuthResponse registerAdmin(AdminRegisterRequest request);

    MessageResponse forgotPassword(ForgotPasswordRequest request);

    MessageResponse resetPassword(ResetPasswordRequest request);

    MessageResponse verifyEmail(VerifyEmailRequest request);

    MessageResponse resendVerification(ResendVerificationRequest request);

    MessageResponse setPasswordWithToken(SetPasswordRequest request);
}
