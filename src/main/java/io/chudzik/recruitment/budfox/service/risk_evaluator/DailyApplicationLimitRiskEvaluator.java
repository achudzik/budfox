package io.chudzik.recruitment.budfox.service.risk_evaluator;

import io.chudzik.recruitment.budfox.activities.ActivityService;
import io.chudzik.recruitment.budfox.exception.RiskyLoanApplicationException;
import io.chudzik.recruitment.budfox.model.LoanApplication;

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

    private final ActivityService activityService;
    private final String ipAddress;
    private final int applicationLimit;


    @Autowired
    public DailyApplicationLimitRiskEvaluator(
            ActivityService activityService,
            HttpServletRequest request,
            @Qualifier("maxApplicationLimit") int applicationLimit) {
        this.activityService = activityService;
        this.ipAddress = request.getRemoteAddr();
        this.applicationLimit = applicationLimit;
    }


    @Transactional(readOnly = true)
    @Override
    protected void doEvaluation(LoanApplication application) throws RiskyLoanApplicationException {
        int applicationsFromIpInLast24Hours = activityService
                .countLoanApplicationsByIpAddressAndEventTimeAfter(application, ipAddress);
        if (applicationsFromIpInLast24Hours >= applicationLimit) {
            throw new RiskyLoanApplicationException("Max applications limit per day reached."); 
        }
    }

}
