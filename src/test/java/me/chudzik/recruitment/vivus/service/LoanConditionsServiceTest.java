package me.chudzik.recruitment.vivus.service;

import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.EXTENSION_PERIOD;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.basicInterest;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.conditionsAfterFirstExtension;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.interestMultiplier;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.loan;
import static org.fest.assertions.api.Assertions.assertThat;
import me.chudzik.recruitment.vivus.model.LoanConditions;
import me.chudzik.recruitment.vivus.service.impl.LoanConditionsServiceImpl;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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
