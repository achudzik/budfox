package me.chudzik.recruitment.vivus.service;

import me.chudzik.recruitment.vivus.model.Loan;
import me.chudzik.recruitment.vivus.model.LoanApplication;

public interface LoanService {

    Loan issueALoan(LoanApplication application);

}
