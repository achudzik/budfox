package io.chudzik.recruitment.budfox.service.risk_evaluator;

import io.chudzik.recruitment.budfox.exception.RiskyLoanApplicationException;
import io.chudzik.recruitment.budfox.model.LoanApplication;
import io.chudzik.recruitment.budfox.service.RiskAssessmentService;

import org.joda.money.Money;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.THREE_PLN;
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.VALID_CLIENT;

// TODO-ach: replace LoanApplication.Builder with LoanApplication.TestDataBuilder 
public class ApplicationTimeAndAmountRiskEvaluatorTest {

    private RiskAssessmentService.RiskEvaluator sut;
    
    static final Money MAX_AMOUNT = THREE_PLN;
    static final LocalTime RISKY_PERIOD_START = new LocalTime(0, 0, 0);
    static final LocalTime RISKY_PERIOD_END = RISKY_PERIOD_START.plusHours(6);
 
    static final Money AMOUNT_BELOW_LIMIT = THREE_PLN.minus(BigDecimal.ONE);
    static final Money AMOUNT_ABOVE_LIMIT = THREE_PLN.plus(BigDecimal.ONE);

    static final DateTime DATE_BEFORE_RISKY_PERIOD = RISKY_PERIOD_START.minusHours(2).toDateTimeToday();
    static final DateTime DATE_IN_RISKY_PERIOD = RISKY_PERIOD_START.plusHours(2).toDateTimeToday();
    static final DateTime DATE_AFTER_RISKY_PERIOD = RISKY_PERIOD_END.plusHours(2).toDateTimeToday();

    @BeforeMethod
    protected void setup() {
        sut = new ApplicationTimeAndAmountRiskEvaluator(MAX_AMOUNT, RISKY_PERIOD_START, RISKY_PERIOD_END);
    }

    @DataProvider
    static final Object[][] validAmountApplications() {
        return new Object[][] {
                { MAX_AMOUNT, DATE_BEFORE_RISKY_PERIOD },
                { MAX_AMOUNT, DATE_AFTER_RISKY_PERIOD },
                { AMOUNT_BELOW_LIMIT, DATE_BEFORE_RISKY_PERIOD },
                { AMOUNT_BELOW_LIMIT, DATE_IN_RISKY_PERIOD },
                { AMOUNT_BELOW_LIMIT, DATE_AFTER_RISKY_PERIOD },
                { AMOUNT_ABOVE_LIMIT, DATE_BEFORE_RISKY_PERIOD },
                { AMOUNT_ABOVE_LIMIT, DATE_AFTER_RISKY_PERIOD }
        };
    }

    @Test(dataProvider = "validAmountApplications")
    public void shouldPassApplicationsWithAmountBelowLimit(Money amount, DateTime date) {
        // arrange
        LoanApplication application = LoanApplication.builder()
                .client(VALID_CLIENT)
                .amount(amount)
                .applicationDate(date)
                .build();

        // act
        sut.evaluate(application);
    }

    @DataProvider
    static final Object[][] invalidAmountApplicationsInRiskyPeriod() {
        return new Object[][] {
                { AMOUNT_ABOVE_LIMIT, RISKY_PERIOD_START.toDateTimeToday() },
                { AMOUNT_ABOVE_LIMIT, DATE_IN_RISKY_PERIOD },
                { AMOUNT_ABOVE_LIMIT, RISKY_PERIOD_END.toDateTimeToday() },
                { MAX_AMOUNT, RISKY_PERIOD_START.toDateTimeToday() },
                { MAX_AMOUNT, DATE_IN_RISKY_PERIOD },
                { MAX_AMOUNT, RISKY_PERIOD_END.toDateTimeToday() },
        };
    }

    @Test(dataProvider = "invalidAmountApplicationsInRiskyPeriod",
            expectedExceptions = RiskyLoanApplicationException.class)
    public void shouldThrowExceptionOnApplicationsAboveLimitInRiskyPeriod(Money amount, DateTime date) {
        // arrange
        LoanApplication application = LoanApplication.builder()
                .client(VALID_CLIENT)
                .amount(amount)
                .applicationDate(date)
                .build();

        // act
        sut.evaluate(application);
    }

}
