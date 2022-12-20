package io.chudzik.recruitment.budfox.loans.risk_evaluator;

import io.chudzik.recruitment.budfox.activities.ActivitiesFacade;
import io.chudzik.recruitment.budfox.loans.LoanApplication;
import io.chudzik.recruitment.budfox.loans.dto.RiskyLoanApplicationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
@Component
public class DailyApplicationLimitRiskEvaluator extends BaseRiskEvaluator {

    private final ActivitiesFacade activitiesFacade;
    private final String ipAddress;
    private final int applicationLimit;


    @Autowired
    public DailyApplicationLimitRiskEvaluator(
            ActivitiesFacade activitiesFacade,
            HttpServletRequest request,
            @Qualifier("maxApplicationLimit") int applicationLimit) {
        this.activitiesFacade = activitiesFacade;
        this.ipAddress = request.getRemoteAddr();
        this.applicationLimit = applicationLimit;
    }


    @Transactional(readOnly = true)
    @Override
    protected void doEvaluation(LoanApplication application) throws RiskyLoanApplicationException {
        long applicationsFromIpInLast24Hours = activitiesFacade
                .countLoanApplicationsByIpAddressAndEventTimeAfter(application, ipAddress);
        if (applicationsFromIpInLast24Hours >= applicationLimit) {
            throw new RiskyLoanApplicationException("Max applications limit per day reached."); 
        }
    }

}
