package io.chudzik.recruitment.budfox.service;

import io.chudzik.recruitment.budfox.exception.RiskyLoanApplicationException;
import io.chudzik.recruitment.budfox.model.LoanApplication;
import io.chudzik.recruitment.budfox.service.risk_evaluator.NoRiskNoGainEvaluator;

import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RiskAssessmentService {

    private RiskEvaluator chain;


    @Autowired
    public RiskAssessmentService(List<RiskEvaluator> evaluators) {
        Preconditions.checkNotNull(evaluators);
        constructChainOfEvaluators(evaluators);
    }


    private void constructChainOfEvaluators(List<RiskEvaluator> evaluators) {
        chain = NoRiskNoGainEvaluator.ACCEPT_ALL_EVALUATOR;
        RiskEvaluator current = chain;
        for (RiskEvaluator evaluator : evaluators) {
            current.setNext(evaluator);
            current = evaluator;
        }
    }


    public void validateApplicationSafety(LoanApplication application) throws RiskyLoanApplicationException {
        chain.evaluate(application);
    }


    public interface RiskEvaluator {

        void evaluate(LoanApplication application) throws RiskyLoanApplicationException;
        void setNext(RiskEvaluator evaluator);

    }

}
