package com.creditas.loan.service;

import com.creditas.loan.response.LoanSimulationResponse;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService{

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendEmail(String recipientEmail, LoanSimulationResponse response) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(recipientEmail);
        helper.setSubject("Resultado da Simulação de Financiamento");
        helper.setText(buildEmailContent(response), true);

        mailSender.send(message);
    }

    private String buildEmailContent(LoanSimulationResponse response) {
        return String.format(
                """
                <p>Olá caro usuário, segue o resultado da sua simulação de financiamento:</p>
                <ul>
                    <li><strong>Total a Pagar:</strong> R$ %.2f</li>
                    <li><strong>Total a Pagar Mensalmente:</strong> R$ %.2f</li>
                    <li><strong>Total de Juros a Pagar:</strong> R$ %.2f</li>
                </ul>
                <p>Obrigado por escolher a Creditas.</p>
                """,
                response.totalPayment(),
                response.monthlyPayment(),
                response.totalInterest()
        );
    }
}
