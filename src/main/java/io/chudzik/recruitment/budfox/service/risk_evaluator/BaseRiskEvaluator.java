package io.chudzik.recruitment.budfox.service.risk_evaluator;

import io.chudzik.recruitment.budfox.exception.RiskyLoanApplicationException;
import io.chudzik.recruitment.budfox.model.LoanApplication;
import io.chudzik.recruitment.budfox.service.RiskAssessmentService.RiskEvaluator;

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
