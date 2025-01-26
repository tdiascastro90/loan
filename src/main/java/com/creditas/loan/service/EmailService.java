package com.creditas.loan.service;

import com.creditas.loan.response.LoanSimulationResponse;
import jakarta.mail.MessagingException;

public interface EmailService {
    void sendEmail(String recipientEmail, LoanSimulationResponse response) throws MessagingException;
}
