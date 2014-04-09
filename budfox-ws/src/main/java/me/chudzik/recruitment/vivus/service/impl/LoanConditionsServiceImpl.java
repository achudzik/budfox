package me.chudzik.recruitment.vivus.service.impl;

import java.math.BigDecimal;

import me.chudzik.recruitment.vivus.model.LoanApplication;
import me.chudzik.recruitment.vivus.model.LoanConditions;
import me.chudzik.recruitment.vivus.service.LoanConditionsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class LoanConditionsServiceImpl implements LoanConditionsService {

    private BigDecimal basicInterest;

    @Autowired
    public LoanConditionsServiceImpl(@Qualifier("basicInterest") BigDecimal basicInterest) {
        this.basicInterest = basicInterest;
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

}
