package io.chudzik.recruitment.budfox.loans.risk_evaluator;

import io.chudzik.recruitment.budfox.loans.LoanApplication;
import io.chudzik.recruitment.budfox.loans.RiskAssessmentService.RiskEvaluator;
import io.chudzik.recruitment.budfox.loans.dto.RiskyLoanApplicationException;

public class NoRiskNoGainEvaluator extends BaseRiskEvaluator {

    public static RiskEvaluator ACCEPT_ALL_EVALUATOR = new NoRiskNoGainEvaluator();


    @Override
    protected void doEvaluation(LoanApplication application) throws RiskyLoanApplicationException { }

}
