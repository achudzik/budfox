package io.chudzik.recruitment.budfox.service

import io.chudzik.recruitment.budfox.BaseUnitSpec
import io.chudzik.recruitment.budfox.model.Loan
import io.chudzik.recruitment.budfox.model.LoanConditions
import io.chudzik.recruitment.budfox.service.impl.LoanConditionsServiceImpl
import spock.lang.Subject

import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.*

class LoanConditionsServiceSpec extends BaseUnitSpec {

    @Subject LoanConditionsService sut = new LoanConditionsServiceImpl(
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
