package io.chudzik.recruitment.budfox.service.impl;

import io.chudzik.recruitment.budfox.model.Loan;
import io.chudzik.recruitment.budfox.model.LoanApplication;
import io.chudzik.recruitment.budfox.model.LoanConditions;
import io.chudzik.recruitment.budfox.service.LoanConditionsService;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class LoanConditionsServiceImpl implements LoanConditionsService {

    private final BigDecimal basicInterest;
    private final BigDecimal interestMultiplier;
    private final Period extensionPeriod;


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
        return LoanConditions.builder()
                .amount(application.getAmount())
                .maturityDate(application.getMaturityDate())
                .interest(basicInterest)
                .build();
    }

    @Override
    public LoanConditions loanExtensionConditions(Loan loan) {
        LoanConditions currentConditions = loan.getConditions();
        return LoanConditions.builder()
                .amount(currentConditions.getAmount())
                .interest(increaseInterest(currentConditions.getInterest()))
                .maturityDate(extendMaturityDate(currentConditions.getMaturityDate()))
                .build();
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
