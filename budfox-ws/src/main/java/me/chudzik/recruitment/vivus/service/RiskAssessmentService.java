package me.chudzik.recruitment.vivus.service;

import me.chudzik.recruitment.vivus.exception.RiskyLoanApplicationException;
import me.chudzik.recruitment.vivus.model.LoanApplication;

public interface RiskAssessmentService {

    boolean isApplicationSafe(LoanApplication application);
    void validateApplicationSafety(LoanApplication application) throws RiskyLoanApplicationException;

}
