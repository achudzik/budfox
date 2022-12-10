package io.chudzik.recruitment.budfox.service;

import io.chudzik.recruitment.budfox.model.LoanConditions;
import io.chudzik.recruitment.budfox.service.impl.LoanConditionsServiceImpl;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;

import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.EXTENSION_PERIOD;
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.basicInterest;
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.conditionsAfterFirstExtension;
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.interestMultiplier;
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.loan;

public class LoanConditionsServiceTest {

    private LoanConditionsService sut;

    @BeforeMethod
    void setup() {
        sut = new LoanConditionsServiceImpl(basicInterest(), interestMultiplier(), EXTENSION_PERIOD);
    }

    // TODO-ach: make it more verbose by creating Loan in-place
    // TODO-ach: add data providers with different values
    @Test
    public void shouldMultiplyInterestWhenExtendingLoan() {
        // act
        LoanConditions result = sut.loanExtensionConditions(loan());

        // assert
        assertThat(result).isLenientEqualsToByIgnoringFields(conditionsAfterFirstExtension(), "id");
    }

}
