package me.chudzik.recruitment.vivus.service;

import me.chudzik.recruitment.vivus.exception.RiskyLoanApplicationException;
import me.chudzik.recruitment.vivus.model.LoanApplication;

public interface RiskAssessmentService {

    void validateApplicationSafety(LoanApplication application) throws RiskyLoanApplicationException;


    public interface RiskEvaluator {

        void evaluate(LoanApplication application) throws RiskyLoanApplicationException;
        void setNext(RiskEvaluator evaluator);

    }
}
