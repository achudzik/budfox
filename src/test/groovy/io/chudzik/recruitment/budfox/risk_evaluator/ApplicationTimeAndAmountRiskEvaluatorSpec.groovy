package io.chudzik.recruitment.budfox.risk_evaluator

import io.chudzik.recruitment.budfox.BaseUnitSpec
import io.chudzik.recruitment.budfox.exception.RiskyLoanApplicationException
import io.chudzik.recruitment.budfox.model.LoanApplication
import io.chudzik.recruitment.budfox.service.risk_evaluator.ApplicationTimeAndAmountRiskEvaluator

import org.joda.money.Money
import org.joda.time.DateTime
import org.joda.time.LocalTime
import spock.lang.Rollup
import spock.lang.Subject

import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.THREE_PLN
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.VALID_CLIENT

// TODO-ach: replace LoanApplication.Builder with LoanApplication.TestDataBuilder
@Rollup
class ApplicationTimeAndAmountRiskEvaluatorSpec extends BaseUnitSpec {

    static final Money MAX_AMOUNT = THREE_PLN
    static final Money AMOUNT_BELOW_LIMIT = THREE_PLN - BigDecimal.ONE
    static final Money AMOUNT_ABOVE_LIMIT = THREE_PLN + BigDecimal.ONE

    static final LocalTime RISKY_PERIOD_START = new LocalTime(0, 0, 0)
    static final LocalTime RISKY_PERIOD_END = RISKY_PERIOD_START.plusHours(6)
    static final DateTime DATE_BEFORE_RISKY_PERIOD = RISKY_PERIOD_START.minusHours(2).toDateTimeToday()
    static final DateTime DATE_IN_RISKY_PERIOD = RISKY_PERIOD_START.plusHours(2).toDateTimeToday()
    static final DateTime DATE_AFTER_RISKY_PERIOD = RISKY_PERIOD_END.plusHours(2).toDateTimeToday()

    @Subject def sut = new ApplicationTimeAndAmountRiskEvaluator(MAX_AMOUNT, RISKY_PERIOD_START, RISKY_PERIOD_END)


    def "should pass application with amount [#amount] below limit"(Money amount, DateTime date) {
        given:
            LoanApplication application = LoanApplication.builder()
                .client(VALID_CLIENT)
                .amount(amount)
                .applicationDate(date)
                .build()
        when:
            sut.evaluate(application)
        then:
            noExceptionThrown()
        where:
            amount             | date
            MAX_AMOUNT         | DATE_BEFORE_RISKY_PERIOD
            MAX_AMOUNT         | DATE_AFTER_RISKY_PERIOD
            AMOUNT_BELOW_LIMIT | DATE_BEFORE_RISKY_PERIOD
            AMOUNT_BELOW_LIMIT | DATE_IN_RISKY_PERIOD
            AMOUNT_BELOW_LIMIT | DATE_AFTER_RISKY_PERIOD
            AMOUNT_ABOVE_LIMIT | DATE_BEFORE_RISKY_PERIOD
            AMOUNT_ABOVE_LIMIT | DATE_AFTER_RISKY_PERIOD

    }


    def "should throw exception on LoanApplication above limit in risky period"(Money amount, DateTime date) {
        given:
            LoanApplication application = LoanApplication.builder()
                .client(VALID_CLIENT)
                .amount(amount)
                .applicationDate(date)
                .build()
        when:
            sut.evaluate(application)
        then:
            thrown(RiskyLoanApplicationException)
        where:
            amount             | date
            MAX_AMOUNT         | RISKY_PERIOD_START.toDateTimeToday()
            MAX_AMOUNT         | DATE_IN_RISKY_PERIOD
            MAX_AMOUNT         | RISKY_PERIOD_END.toDateTimeToday()
            AMOUNT_ABOVE_LIMIT | RISKY_PERIOD_START.toDateTimeToday()
            AMOUNT_ABOVE_LIMIT | DATE_IN_RISKY_PERIOD
            AMOUNT_ABOVE_LIMIT | RISKY_PERIOD_END.toDateTimeToday()
    }

}
