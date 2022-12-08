package me.chudzik.recruitment.vivus.service.risk_evaluator;

import me.chudzik.recruitment.vivus.exception.RiskyLoanApplicationException;
import me.chudzik.recruitment.vivus.model.LoanApplication;
import me.chudzik.recruitment.vivus.service.RiskAssessmentService.RiskEvaluator;

public class NoRiskNoGainEvaluator extends BaseRiskEvaluator {

    public static RiskEvaluator ACCEPT_ALL_EVALUATOR = new NoRiskNoGainEvaluator();

    @Override
    protected void doEvaluation(LoanApplication application) throws RiskyLoanApplicationException { }

}
