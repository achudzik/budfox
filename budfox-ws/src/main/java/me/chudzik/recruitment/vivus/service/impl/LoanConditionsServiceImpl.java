package me.chudzik.recruitment.vivus.service.impl;

import java.math.BigDecimal;

import me.chudzik.recruitment.vivus.model.Loan;
import me.chudzik.recruitment.vivus.model.LoanApplication;
import me.chudzik.recruitment.vivus.model.LoanConditions;
import me.chudzik.recruitment.vivus.service.LoanConditionsService;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class LoanConditionsServiceImpl implements LoanConditionsService {

    private BigDecimal basicInterest;
    private BigDecimal interestMultiplier;
    private Period extensionPeriod;

    @Autowired
    public LoanConditionsServiceImpl(
            @Qualifier("basicInterest") BigDecimal basicInterest,
            @Qualifier("interestMultiplier") BigDecimal interestMultiplier,
            @Qualifier("extensionPeriod") Period extensionPeriod) {
        this.basicInterest = basicInterest;
        this.interestMultiplier = interestMultiplier;
        this.extensionPeriod = extensionPeriod;
    }

    // FIXME-ach: test that!
    @Override
    public LoanConditions calculateInitialLoanConditions(LoanApplication application) {
        LoanConditions conditions = LoanConditions.builder()
                .amount(application.getAmount())
                .maturityDate(application.getMaturityDate())
                .interest(basicInterest)
                .build();
        return conditions;
    }

    @Override
    public LoanConditions loanExtensionConditions(Loan loan) {
        LoanConditions currentConditions = loan.getConditions();
        LoanConditions newConditions = LoanConditions.builder()
                .amount(currentConditions.getAmount())
                .interest(increaseInterest(currentConditions.getInterest()))
                .maturityDate(extendMaturityDate(currentConditions.getMaturityDate()))
                .build();
        return newConditions;
    }

    // XXX-ach: move to helper
    private BigDecimal increaseInterest(BigDecimal currentInterest) {
        return currentInterest.multiply(interestMultiplier);
    }

    // XXX-ach: move to helper
    private DateTime extendMaturityDate(DateTime currentDate) {
        return currentDate.plus(extensionPeriod);
    }

}
