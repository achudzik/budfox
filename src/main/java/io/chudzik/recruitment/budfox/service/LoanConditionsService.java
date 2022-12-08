package io.chudzik.recruitment.budfox.service;

import io.chudzik.recruitment.budfox.model.Loan;
import io.chudzik.recruitment.budfox.model.LoanApplication;
import io.chudzik.recruitment.budfox.model.LoanConditions;

public interface LoanConditionsService {

    LoanConditions calculateInitialLoanConditions(LoanApplication application);

    LoanConditions loanExtensionConditions(Loan loan);

}
