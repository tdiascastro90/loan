package com.creditas.loan.validate;

import com.creditas.loan.request.LoanSimulationRequest;

import java.util.List;

public interface Validate {
    void validate(List<LoanSimulationRequest> loanSimulationRequest);
}
