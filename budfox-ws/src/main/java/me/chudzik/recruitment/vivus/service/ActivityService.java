package me.chudzik.recruitment.vivus.service;

import javax.servlet.http.HttpServletRequest;

public interface ActivityService {

    void logLoanApplication(Long clientId, HttpServletRequest request);
    void logLoanExtension(Long loanId, HttpServletRequest request);

}
