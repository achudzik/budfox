package me.chudzik.recruitment.vivus.service.impl;

import static me.chudzik.recruitment.vivus.service.risk_evaluator.NoRiskNoGainEvaluator.ACCEPT_ALL_EVALUATOR;

import java.util.List;

import me.chudzik.recruitment.vivus.exception.RiskyLoanApplicationException;
import me.chudzik.recruitment.vivus.model.LoanApplication;
import me.chudzik.recruitment.vivus.service.RiskAssessmentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;

@Service
public class RiskAssessmentServiceImpl implements RiskAssessmentService {

    private RiskEvaluator chain;

    @Autowired
    public RiskAssessmentServiceImpl(List<RiskEvaluator> evaluators) {
        Preconditions.checkNotNull(evaluators);
        constructChainOfEvaluators(evaluators);
    }

    private void constructChainOfEvaluators(List<RiskEvaluator> evaluators) {
        chain = ACCEPT_ALL_EVALUATOR;
        RiskEvaluator current = chain; 
        for (RiskEvaluator evaluator : evaluators) {
            current.setNext(evaluator);
            current = evaluator;
        }
    }

    @Override
    public void validateApplicationSafety(LoanApplication application) throws RiskyLoanApplicationException {
        chain.evaluate(application);
    }

}
