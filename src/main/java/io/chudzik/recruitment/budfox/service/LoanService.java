package io.chudzik.recruitment.budfox.service;

import io.chudzik.recruitment.budfox.exception.LoanNotFoundException;
import io.chudzik.recruitment.budfox.model.Loan;
import io.chudzik.recruitment.budfox.model.LoanApplication;

public interface LoanService {

    Loan issueALoan(LoanApplication application);

    Loan extendLoan(Long loanId) throws LoanNotFoundException;

}
