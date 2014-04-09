package me.chudzik.recruitment.vivus.service.risk_evaluator;

import me.chudzik.recruitment.vivus.exception.RiskyLoanApplicationException;
import me.chudzik.recruitment.vivus.model.LoanApplication;
import me.chudzik.recruitment.vivus.service.RiskAssessmentService.RiskEvaluator;

public abstract class BaseRiskEvaluator implements RiskEvaluator {

    private RiskEvaluator next;

    public void setNext(RiskEvaluator evaluator) {
        this.next = evaluator;
    }

    public void evaluate(LoanApplication application) throws RiskyLoanApplicationException {
        doEvaluation(application);
        if (null != next) {
            next.evaluate(application);
        }
    }

    abstract protected void doEvaluation(LoanApplication application) throws RiskyLoanApplicationException;

}
