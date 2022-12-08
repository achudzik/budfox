package io.chudzik.recruitment.budfox.service;

import io.chudzik.recruitment.budfox.exception.RiskyLoanApplicationException;
import io.chudzik.recruitment.budfox.model.LoanApplication;

public interface RiskAssessmentService {

    void validateApplicationSafety(LoanApplication application) throws RiskyLoanApplicationException;


    public interface RiskEvaluator {

        void evaluate(LoanApplication application) throws RiskyLoanApplicationException;
        void setNext(RiskEvaluator evaluator);

    }
}
