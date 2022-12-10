package io.chudzik.recruitment.budfox.service.risk_evaluator;

import io.chudzik.recruitment.budfox.exception.RiskyLoanApplicationException;
import io.chudzik.recruitment.budfox.model.LoanApplication;

import org.joda.money.Money;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ApplicationTimeAndAmountRiskEvaluator extends BaseRiskEvaluator {

    private Money maxAmount;
    private LocalTime riskyPeriodStart;
    private LocalTime riskyPeriodEnd;

    @Autowired
    public ApplicationTimeAndAmountRiskEvaluator(
            @Qualifier("maxAmount") Money maxAmount,
            @Qualifier("riskyPeriodStart") LocalTime riskyPeriodStart,
            @Qualifier("riskyPeriodEnd") LocalTime riskyPeriodEnd) {
        this.maxAmount = maxAmount;
        this.riskyPeriodStart = riskyPeriodStart;
        this.riskyPeriodEnd = riskyPeriodEnd;
    }

    @Override
    protected void doEvaluation(LoanApplication application) throws RiskyLoanApplicationException {
        if (isInCheckedTimeWindow(application) && isOnMaxPossibleAmount(application)) {
            throw new RiskyLoanApplicationException("Max amount in risky time period.");
        }
    }

    private boolean isInCheckedTimeWindow(LoanApplication application) {
        LocalTime applicationTime = application.getApplicationDate().toLocalTime();
        return (applicationTime.isEqual(riskyPeriodStart) || applicationTime.isAfter(riskyPeriodStart))
                && (applicationTime.isBefore(riskyPeriodEnd) || applicationTime.isEqual(riskyPeriodEnd));
    }

    private boolean isOnMaxPossibleAmount(LoanApplication application) {
        return !application.getAmount().isLessThan(maxAmount);
    }
}
