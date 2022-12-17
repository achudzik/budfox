package io.chudzik.recruitment.budfox.loans

import io.chudzik.recruitment.budfox.BaseUnitSpec

import spock.lang.Subject

import static io.chudzik.recruitment.budfox.commons.tests.PreExistingEntities.EXTENSION_PERIOD
import static io.chudzik.recruitment.budfox.commons.tests.PreExistingEntities.basicInterest
import static io.chudzik.recruitment.budfox.commons.tests.PreExistingEntities.interestMultiplier
import static io.chudzik.recruitment.budfox.commons.tests.PreExistingEntities.loan

class LoanConditionsServiceSpec extends BaseUnitSpec {

    @Subject def sut = new LoanConditionsService(
            basicInterest(),
            interestMultiplier(),
            EXTENSION_PERIOD
    )


    // TODO-ach: make it more verbose by creating Loan in-place
    // TODO-ach: add data providers with different values
    def "should multiply interest when extending loan"() {
        given:
            final Loan loan = loan()
        when:
            LoanConditions result = sut.loanExtensionConditions(loan)
        then:
            result.interest > loan.conditions.interest
    }

}
