package me.chudzik.recruitment.vivus.utils;

import static me.chudzik.recruitment.vivus.model.Activity.ActivityType.LOAN_APPLICATION;

import java.math.BigDecimal;

import me.chudzik.recruitment.vivus.model.Activity;
import me.chudzik.recruitment.vivus.model.Client;
import me.chudzik.recruitment.vivus.model.Loan;
import me.chudzik.recruitment.vivus.model.LoanApplication;
import me.chudzik.recruitment.vivus.model.LoanConditions;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.time.DateTime;
import org.joda.time.Period;

public class PreExistingEntities {

    public static final String VALID_PESEL = "68092005286";

    public static final Long VALID_ID = new Long(1);
    
    public static final Client VALID_CLIENT = Client.builder()
            .id(VALID_ID)
            .identificationNumber(VALID_PESEL)
            .build();

    public static final DateTime TODAY = new DateTime(2014, 4, 7, 13, 5);

    public static final DateTime YESTERDAY = TODAY.minusDays(1);

    public static final DateTime MONTH_LATER = TODAY.plusMonths(1);

    public static final DateTime MONTH_AND_A_WEEK_LATER = MONTH_LATER.plusWeeks(1);

    public static final DateTime MONTH_AND_A_TWO_WEEKS_LATER = MONTH_LATER.plusWeeks(2);

    public static final DateTime YEAR_LATER = TODAY.plusMonths(12);

    public static final Period THREE_WEEKS_PERIOD = Period.weeks(3);

    public static final Money THREE_PLN = Money.of(CurrencyUnit.of("PLN"), new BigDecimal("3.0"));

    public static final Money ELEVEN_PLN = Money.of(CurrencyUnit.of("PLN"), new BigDecimal("11.0"));

    public static final LoanApplication VALID_LOAN_APPLICATION = LoanApplication.builder()
            .client(VALID_CLIENT)
            .amount(THREE_PLN)
            .maturityDate(MONTH_LATER)
            .term(THREE_WEEKS_PERIOD)
            .build();

    public static final BigDecimal BASIC_INTEREST = new BigDecimal("10.0");

    public static final LoanConditions VALID_LOAN_CONDITIONS = LoanConditions.builder()
            .amount(THREE_PLN)
            .interest(BASIC_INTEREST)
            .maturityDate(MONTH_LATER)
            .build();

    public static final Loan VALID_LOAN = Loan.builder()
            .id(VALID_ID)
            .client(VALID_CLIENT)
            .conditions(VALID_LOAN_CONDITIONS)
            .build();

    public static final String LOCAL_IP_ADDRESS = "127.0.0.1";

    public static final Activity VALID_ACTIVITY = Activity.builder()
            .client(VALID_CLIENT)
            .type(LOAN_APPLICATION)
            .ipAddress(LOCAL_IP_ADDRESS)
            .build();

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
