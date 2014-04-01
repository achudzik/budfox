package me.chudzik.recruitment.vivus.utils;

import java.math.BigDecimal;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.time.Period;

import me.chudzik.recruitment.vivus.model.Client;
import me.chudzik.recruitment.vivus.model.LoanApplication;

public class PreExistingEntities {

    public static final String VALID_PESEL = "68092005286";

    public static final Client VALID_CLIENT =
            new Client.Builder().id(1l).identificationNumber(VALID_PESEL).build();

    public static final Period THREE_WEEKS_PERIOD = Period.weeks(3);

    public static final Money THREE_PLN = Money.of(CurrencyUnit.of("PLN"), new BigDecimal("3.0"));

    public static final LoanApplication VALID_LOAN_APPLICATION =
            new LoanApplication.Builder().client(VALID_CLIENT).amount(THREE_PLN).term(THREE_WEEKS_PERIOD).build();
}
