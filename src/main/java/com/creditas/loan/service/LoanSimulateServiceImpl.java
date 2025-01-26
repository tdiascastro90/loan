package com.creditas.loan.service;

import com.creditas.loan.request.LoanSimulationRequest;
import com.creditas.loan.response.LoanSimulationResponse;
import jakarta.mail.MessagingException;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class LoanSimulateServiceImpl implements LoanSimulateService {

    private static final Logger logger = LoggerFactory.getLogger(LoanSimulateServiceImpl.class);
    private final Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    @Autowired
    private EmailService emailService;

    @Override
    public List<LoanSimulationResponse> simulateLoans(List<LoanSimulationRequest> requests) {
        List<CompletableFuture<LoanSimulationResponse>> futures = requests.stream()
                .map(request -> CompletableFuture.supplyAsync(() -> {
                    LoanSimulationResponse response = simulateLoan(request);
                    sendEmailWithSimulationResult(response);
                    return response;
                }, executor))
                .toList();

        return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    private LoanSimulationResponse simulateLoan(LoanSimulationRequest request) {
        logger.info("Simulation loan for request: {}", request);
        var birthDateParsed = LocalDate.parse(request.birthDate());
        double annualInterestRate = calculateAnnualInterestRate(birthDateParsed);
        double monthlyInterestRate = annualInterestRate / 12 / 100;
        int totalMonths = request.period();


        logger.info("Calculate PMT");
        double monthlyPayment = (request.amount() * monthlyInterestRate) /
                (1 - Math.pow(1 + monthlyInterestRate, -totalMonths));

        double totalPayment = monthlyPayment * totalMonths;
        double totalInterest = totalPayment - request.amount();

        return new LoanSimulationResponse(Math.round(totalPayment * 100.0) / 100.0, Math.round(monthlyPayment * 100.0) / 100.0, Math.round(totalInterest * 100.0) / 100.0);
    }


    private double calculateAnnualInterestRate(LocalDate birthDate) {
        int age = Period.between(birthDate, LocalDate.now()).getYears();

        if (age <= 25) {
            return 5.0;
        } else if (age <= 40) {
            return 3.0;
        } else if (age <= 60) {
            return 2.0;
        } else {
            return 4.0;
        }
    }

    private void sendEmailWithSimulationResult(LoanSimulationResponse response) {
        try {
            emailService.sendEmail("tdiascastro90@gmail.com", response);
        } catch (MessagingException e) {
            logger.error("Erro ao enviar o e-mail: {}", e.getMessage());
        }
    }

    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }
}
