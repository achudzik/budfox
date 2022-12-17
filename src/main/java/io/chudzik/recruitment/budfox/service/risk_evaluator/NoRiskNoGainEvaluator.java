package io.chudzik.recruitment.budfox.service.risk_evaluator;

import io.chudzik.recruitment.budfox.exception.RiskyLoanApplicationException;
import io.chudzik.recruitment.budfox.model.LoanApplication;
import io.chudzik.recruitment.budfox.service.RiskAssessmentService.RiskEvaluator;

public class NoRiskNoGainEvaluator extends BaseRiskEvaluator {

    public static RiskEvaluator ACCEPT_ALL_EVALUATOR = new NoRiskNoGainEvaluator();


    @Override
    protected void doEvaluation(LoanApplication application) throws RiskyLoanApplicationException { }

}
