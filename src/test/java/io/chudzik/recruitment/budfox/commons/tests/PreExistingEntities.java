package io.chudzik.recruitment.budfox.commons.tests;

import io.chudzik.recruitment.budfox.activities.Activity;
import io.chudzik.recruitment.budfox.activities.ActivityType;
import io.chudzik.recruitment.budfox.clients.Client;
import io.chudzik.recruitment.budfox.loans.Loan;
import io.chudzik.recruitment.budfox.loans.LoanApplication;
import io.chudzik.recruitment.budfox.loans.LoanConditions;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;

import java.math.BigDecimal;
import java.time.ZoneId;

import static org.joda.time.DateTimeZone.UTC;

public class PreExistingEntities {

    public static final String VALID_PESEL = "68092005286";

    private static final Long VALID_ID = 0L;


    public static Long validId() {
        return VALID_ID;
    }

    public static Long invalidId() {
        return Long.valueOf(-7312L);
    }


    public static Client client() {
        return Client.builder()
                .id(validId())
                .identificationNumber(VALID_PESEL)
                .build();
    }

    public static final Client VALID_CLIENT = Client.builder()
            .id(validId())
            .identificationNumber(VALID_PESEL)
            .build();

    public static final DateTimeZone DEFAULT_TIMEZONE = UTC;
    public static final ZoneId DEFAULT_ZONE_ID = UTC.toTimeZone().toZoneId();

    public static final DateTime TODAY = new DateTime(2014, 4, 7, 21, 37, DEFAULT_TIMEZONE);

    public static final DateTime YESTERDAY = TODAY.minusDays(1);

    public static final DateTime MONTH_LATER = TODAY.plusMonths(1);

    public static final DateTime MONTH_AND_A_WEEK_LATER = MONTH_LATER.plusWeeks(1);

    public static final DateTime MONTH_AND_A_TWO_WEEKS_LATER = MONTH_LATER.plusWeeks(2);

    public static final DateTime YEAR_LATER = TODAY.plusMonths(12);

    public static final Period THREE_WEEKS_PERIOD = Period.weeks(3);

    public static final CurrencyUnit PLN = CurrencyUnit.of("PLN");
    public static final Money THREE_PLN = Money.of(PLN, new BigDecimal("3.0"));
    public static final Money ELEVEN_PLN = Money.of(PLN, new BigDecimal("11.0"));

    public static LoanApplication loanApplication() {
        return LoanApplication.builder()
            .client(client())
            .amount(THREE_PLN)
            .maturityDate(MONTH_LATER)
            .term(THREE_WEEKS_PERIOD)
            .build();
    }

    public static final LoanApplication VALID_LOAN_APPLICATION = LoanApplication.builder()
            .client(VALID_CLIENT)
            .amount(THREE_PLN)
            .maturityDate(MONTH_LATER)
            .term(THREE_WEEKS_PERIOD)
            .build();

    public static final BigDecimal BASIC_INTEREST = new BigDecimal("10.0");

    public static final String LOCAL_IP_ADDRESS = "127.0.0.1";

    public static Activity loanApplicationActivity() {
        return Activity.builder()
                .client(client())
                .type(ActivityType.LOAN_APPLICATION)
                .ipAddress(LOCAL_IP_ADDRESS)
                .build();
    }

    public static Activity loanExtensionActivity() {
        return Activity.builder()
                .client(client())
                .type(ActivityType.LOAN_EXTENSION)
                .ipAddress(LOCAL_IP_ADDRESS)
                .build();
    }

    public static LoanConditions basicConditions() {
        return LoanConditions.builder()
                .amount(THREE_PLN)
                .interest(basicInterest())
                .maturityDate(MONTH_LATER)
                .build();
    }

    public static BigDecimal basicInterest() {
        return BASIC_INTEREST;
    }

    public static final Period EXTENSION_PERIOD = Period.parse("P1W");

    public static LoanConditions conditionsAfterFirstExtension() {
        return LoanConditions.builder()
                .id(validId() + 1)
                .amount(THREE_PLN)
                .interest(interestAfterFirstExtension())
                .maturityDate(MONTH_LATER.plus(EXTENSION_PERIOD))
                .build();
    }

    public static BigDecimal interestAfterFirstExtension() {
        return basicInterest().multiply(interestMultiplier());
    }

    public static BigDecimal interestMultiplier() {
        return new BigDecimal("1.5");
    }

    public static LoanConditions conditionsAfterSecondExtension() {
        return LoanConditions.builder()
                .id(validId() + 2)
                .amount(THREE_PLN)
                .interest(interestAfterSecondExtension())
                // XXX-ach: not so pretty
                .maturityDate(MONTH_LATER.plus(EXTENSION_PERIOD).plus(EXTENSION_PERIOD))
                .build();
    }

    public static BigDecimal interestAfterSecondExtension() {
        return interestAfterFirstExtension().multiply(interestMultiplier());
    }

    public static Loan unsavedLoan() {
        Loan loan = Loan.builder()
                .conditions(basicConditions())
                .build();
        client().addLoan(loan);
        return loan;
    }

    public static Loan loan() {
        Loan loan = Loan.builder()
                .id(validId())
                .conditions(basicConditions())
                .build();
        client().addLoan(loan);
        return loan;
    }

    public static Loan loanAfterFirstExtension() {
        Loan loan = loan();
        loan.setCondition(conditionsAfterFirstExtension());
        return loan;
    }

    public static Loan loanAfterSecondExtension() {
        Loan loan = loan();
        loan.setCondition(conditionsAfterSecondExtension());
        return loan;
    }

    public static Client clientWithOneLoan() {
        Client client = client();
        client.addLoan(loan());
        return client;
    }

    public static final Client CLIENT_WITH_LOANS = Client.builder()
            .id(2L)
            .identificationNumber("71093013682")
            .withLoan(Loan.builder()
                    .id(2L)
                    .conditions(LoanConditions.builder()
                            .amount(THREE_PLN)
                            .interest(BASIC_INTEREST)
                            .maturityDate(MONTH_LATER)
                            .build())
                    .build())
            .withLoan(Loan.builder()
                    .id(3L)
                    .conditions(LoanConditions.builder()
                            .amount(ELEVEN_PLN)
                            .interest(BASIC_INTEREST)
                            .maturityDate(YEAR_LATER)
                            .build())
                    .build())
            .build();

}
