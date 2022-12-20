package io.chudzik.recruitment.budfox.activities;

import io.chudzik.recruitment.budfox.activities.dto.LogActivityRequest;
import io.chudzik.recruitment.budfox.loans.LoanApplication;

import lombok.RequiredArgsConstructor;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
public class ActivitiesFacade {

    private final ActivityService activityService;


    public long count() {
        return activityService.count();
    }

    public long countLoanApplicationsByIpAddressAndEventTimeAfter(LoanApplication application, String ipAddress) {
        return activityService.countLoanApplicationsByIpAddressAndEventTimeAfter(application, ipAddress);
    }


    public void logLoanApplication(Long clientId, HttpServletRequest request) {
        activityService.logLoanApplication(clientId, request);
    }

    public void logLoanExtension(Long loanId, HttpServletRequest request) {
        activityService.logLoanExtension(loanId, request);
    }


    public void newActivity(LogActivityRequest request) {
        activityService.logActivity(request);
    }

}
