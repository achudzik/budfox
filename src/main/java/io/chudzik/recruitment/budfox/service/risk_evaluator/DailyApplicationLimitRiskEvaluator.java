package io.chudzik.recruitment.budfox.service.risk_evaluator;

import io.chudzik.recruitment.budfox.exception.RiskyLoanApplicationException;
import io.chudzik.recruitment.budfox.model.Activity.ActivityType;
import io.chudzik.recruitment.budfox.model.LoanApplication;
import io.chudzik.recruitment.budfox.repository.ActivityRepository;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
public class DailyApplicationLimitRiskEvaluator extends BaseRiskEvaluator {

    private final ActivityRepository activityRepository;
    private final String ipAddress;
    private final Integer applicationLimit;


    @Autowired
    public DailyApplicationLimitRiskEvaluator(
            ActivityRepository activityRepository,
            HttpServletRequest request,
            @Qualifier("maxApplicationLimit") Integer applicationLimit) {
        this.activityRepository = activityRepository;
        this.ipAddress = request.getRemoteAddr();
        this.applicationLimit = applicationLimit;
    }


    @Transactional(readOnly = true)
    @Override
    protected void doEvaluation(LoanApplication application) throws RiskyLoanApplicationException {
        // XXX-ach: lenght of given period can also be a injectable parameter
        DateTime twentyFourHoursEarlier = application.getApplicationDate().minusDays(1);
        int applicationsFromIpInLast24Hours = activityRepository
                .countByTypeAndIpAddressAndEventTimeAfter(ActivityType.LOAN_APPLICATION, ipAddress, twentyFourHoursEarlier);
        if (applicationsFromIpInLast24Hours > applicationLimit) {
            throw new RiskyLoanApplicationException("Max applications limit per day reached."); 
        }
    }

}
