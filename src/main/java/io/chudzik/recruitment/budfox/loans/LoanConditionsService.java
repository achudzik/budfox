package io.chudzik.recruitment.budfox.loans;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class LoanConditionsService {

    private final BigDecimal basicInterest;
    private final BigDecimal interestMultiplier;
    private final Period extensionPeriod;


    @Autowired
    public LoanConditionsService(
            @Qualifier("basicInterest") BigDecimal basicInterest,
            @Qualifier("interestMultiplier") BigDecimal interestMultiplier,
            @Qualifier("extensionPeriod") Period extensionPeriod) {
        this.basicInterest = basicInterest;
        this.interestMultiplier = interestMultiplier;
        this.extensionPeriod = extensionPeriod;
    }


    // FIXME-ach: test that!
    public LoanConditions calculateInitialLoanConditions(LoanApplication application) {
        return LoanConditions.builder()
                .amount(application.getAmount())
                .maturityDate(application.getMaturityDate())
                .interest(basicInterest)
                .build();
    }


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
