package com.creditas.loan.validate;

import com.creditas.loan.request.LoanSimulationRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class ValidateImpl implements Validate{

    @Override
    public void validate(List<LoanSimulationRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("Request payload cannot be empty");
        }

        requests.forEach(this::validatreSingleRequest);
    }

    private void validatreSingleRequest(LoanSimulationRequest request) {
        validateAmount(request.amount());
        validateBirthDate(request.birthDate());
        validatePeriod(request.period());
    }

    private void validateAmount(Double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
    }

    private void validateBirthDate(String birthDate) {
        if (birthDate == null || birthDate.isEmpty()) {
            throw new IllegalArgumentException("Birth date must not be null or empty");
        }
        try {
            LocalDate.parse(birthDate);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid birth date format. It must be yyyy-MM-dd");
        }
    }

    private void validatePeriod(Integer period) {
        if (period <= 0) {
            throw new IllegalArgumentException("Period must be greater than 0");
        }
    }
}
