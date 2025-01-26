package com.creditas.loan.service;

import com.creditas.loan.request.LoanSimulationRequest;
import com.creditas.loan.response.LoanSimulationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LoanSimulateServiceTest {

    private LoanSimulateServiceImpl loanSimulateService;

    @Mock
    private EmailService emailServiceMock;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        loanSimulateService = new LoanSimulateServiceImpl();
        loanSimulateService.setEmailService(emailServiceMock);
    }

    @Test
    public void testSimulateLoansSuccess() {
        LoanSimulationRequest request1 = new LoanSimulationRequest(10000.0, "1990-01-01", 12);
        LoanSimulationRequest request2 = new LoanSimulationRequest(15000.0, "1980-05-10", 24);

        List<LoanSimulationRequest> requests = List.of(request1, request2);

        List<LoanSimulationResponse> responses = loanSimulateService.simulateLoans(requests);

        assertNotNull(responses);
        assertEquals(2, responses.size());

        LoanSimulationResponse response1 = responses.get(0);
        LoanSimulationResponse response2 = responses.get(1);

        assertEquals(10163.24, response1.totalPayment());
        assertEquals(846.94, response1.monthlyPayment());
        assertEquals(163.24, response1.totalInterest());

        assertEquals(15314.49, response2.totalPayment());
        assertEquals(638.1, response2.monthlyPayment());
        assertEquals(314.49, response2.totalInterest());
    }

    @Test
    public void testSimulateLoanWithInvalidBirthDate() {
        LoanSimulationRequest invalidRequest = new LoanSimulationRequest(10000.0, "invalid-date", 12);

        Exception exception = assertThrows(RuntimeException.class, () ->
                loanSimulateService.simulateLoans(List.of(invalidRequest)));

        assertTrue(exception.getMessage().contains("Text 'invalid-date' could not be parsed"));
    }

    @Test
    public void testSimulateLoanWithNegativeAmount() {
        LoanSimulationRequest invalidRequest = new LoanSimulationRequest(-10000.0, "1990-01-01", 12);

        List<LoanSimulationResponse> responses = loanSimulateService.simulateLoans(List.of(invalidRequest));

        LoanSimulationResponse response = responses.get(0);

        assertNotNull(response);
        assertTrue(response.totalPayment() < 0);
        assertTrue(response.totalInterest() < 0);
    }


    @Test
    public void testSimulateLoansEmptyList() {
        List<LoanSimulationRequest> requests = List.of();

        List<LoanSimulationResponse> responses = loanSimulateService.simulateLoans(requests);

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }
}