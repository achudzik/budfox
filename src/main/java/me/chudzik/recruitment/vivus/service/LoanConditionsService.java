package me.chudzik.recruitment.vivus.service;

import me.chudzik.recruitment.vivus.model.Loan;
import me.chudzik.recruitment.vivus.model.LoanApplication;
import me.chudzik.recruitment.vivus.model.LoanConditions;

public interface LoanConditionsService {

    LoanConditions calculateInitialLoanConditions(LoanApplication application);

    LoanConditions loanExtensionConditions(Loan loan);

}
