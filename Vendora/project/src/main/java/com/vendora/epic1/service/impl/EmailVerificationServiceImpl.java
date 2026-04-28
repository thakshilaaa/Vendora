package com.vendora.epic1.service.impl;

import com.vendora.epic1.dto.auth.MessageResponse;
import com.vendora.epic1.dto.auth.VerifyEmailRequest;
import com.vendora.epic1.exception.InvalidTokenException;
import com.vendora.epic1.model.EmailVerificationToken;
import com.vendora.epic1.model.User;
import com.vendora.epic1.model.enums.UserStatus;
import com.vendora.epic1.repository.EmailVerificationTokenRepository;
import com.vendora.epic1.repository.UserRepository;
import com.vendora.epic1.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public MessageResponse verifyEmail(VerifyEmailRequest request) {
        EmailVerificationToken token = emailVerificationTokenRepository.findByToken(request.getToken().trim())
                .orElseThrow(() -> new InvalidTokenException("Invalid verification token"));

        User user = token.getUser();

        if (token.getExpiryDate().isBefore(Instant.now())) {
            emailVerificationTokenRepository.delete(token);
            throw new InvalidTokenException("Verification token has expired");
        }
        user.setEmailVerified(true);
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        emailVerificationTokenRepository.delete(token);

        return new MessageResponse("Email verified successfully");
    }
}
