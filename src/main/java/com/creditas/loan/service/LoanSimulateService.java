package com.creditas.loan.service;

import com.creditas.loan.request.LoanSimulationRequest;
import com.creditas.loan.response.LoanSimulationResponse;

import java.util.List;

public interface LoanSimulateService {
    List<LoanSimulationResponse> simulateLoans(List<LoanSimulationRequest> loanSimulationRequest);
}
