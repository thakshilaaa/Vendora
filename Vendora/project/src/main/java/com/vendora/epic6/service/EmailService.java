package com.vendora.epic6.service;

public interface EmailService {

    void sendApprovalEmail(String toEmail, String companyName, String tempPassword);

    void sendRejectionEmail(String toEmail, String companyName);
}
