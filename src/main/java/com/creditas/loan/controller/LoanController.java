package com.creditas.loan.controller;

import com.creditas.loan.error_util.ValidateException;
import com.creditas.loan.request.LoanSimulationRequest;
import com.creditas.loan.response.LoanSimulationResponse;
import com.creditas.loan.service.LoanSimulateService;
import com.creditas.loan.validate.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loan")
public class LoanController {

    private static final Logger logger = LoggerFactory.getLogger(LoanController.class);
    private final LoanSimulateService loanSimulateService;
    private final Validate validate;

    public LoanController(LoanSimulateService loanSimulateService, Validate validate) {
        this.loanSimulateService = loanSimulateService;
        this.validate = validate;
    }

    @PostMapping("/simulate")
    public List<LoanSimulationResponse> create(@RequestBody List<LoanSimulationRequest> loanSimulationRequestList) {
        logger.info("Received loan simulation request: {}", loanSimulationRequestList);

        try {
            validate.validate(loanSimulationRequestList);
        } catch (Exception e) {
            logger.error("Error validating loan simulation request: {}", e.getMessage());
            throw new ValidateException(e.getMessage());
        }

        var responseList = loanSimulateService.simulateLoans(loanSimulationRequestList);
        logger.info("Loan simulation response: {}", responseList);
        return responseList;
    }
}
