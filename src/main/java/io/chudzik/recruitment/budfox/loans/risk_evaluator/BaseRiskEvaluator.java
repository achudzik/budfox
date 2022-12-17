package io.chudzik.recruitment.budfox.loans.risk_evaluator;

import io.chudzik.recruitment.budfox.loans.LoanApplication;
import io.chudzik.recruitment.budfox.loans.RiskAssessmentService.RiskEvaluator;
import io.chudzik.recruitment.budfox.loans.dto.RiskyLoanApplicationException;

import lombok.Setter;

@Setter
public abstract class BaseRiskEvaluator implements RiskEvaluator {

    private RiskEvaluator next;


    public void evaluate(LoanApplication application) throws RiskyLoanApplicationException {
        doEvaluation(application);
        if (null != next) {
            next.evaluate(application);
        }
    }

    abstract protected void doEvaluation(LoanApplication application) throws RiskyLoanApplicationException;

}
