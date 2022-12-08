package me.chudzik.recruitment.vivus.service.risk_evaluator;

import javax.servlet.http.HttpServletRequest;

import me.chudzik.recruitment.vivus.exception.RiskyLoanApplicationException;
import me.chudzik.recruitment.vivus.model.Activity.ActivityType;
import me.chudzik.recruitment.vivus.model.LoanApplication;
import me.chudzik.recruitment.vivus.repository.ActivityRepository;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
public class DailyApplicationLimitRiskEvaluator extends BaseRiskEvaluator {

    private ActivityRepository activityRepository;
    private String ipAddress;
    private Integer applicationLimit;

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
