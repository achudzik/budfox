package me.chudzik.recruitment.vivus.configuration;

import java.math.BigDecimal;

import org.joda.money.Money;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

// XXX-ach: replace with configurationService, providing dynamic parameterization support
@Configuration
public class BusinessConfiguration {

    @Autowired
    private Environment env;

    @Bean
    public BigDecimal basicInterest() {
        return env.getRequiredProperty("loan.interest.basic", BigDecimal.class);
    }

    @Bean
    public BigDecimal interestMultiplier() {
        return env.getRequiredProperty("loan.interest.multiplier", BigDecimal.class);
    }

    @Bean
    public Integer maxApplicationLimit() {
        return env.getRequiredProperty("riskEvaluation.applications.dailyLimit", Integer.class);
    }

    @Bean
    public Money maxAmount() {
        String maxAmount = env.getRequiredProperty("riskEvaluation.applications.maxAmount");
        return Money.parse(maxAmount);
    }

    @Bean
    public LocalTime riskyPeriodStart() {
        String periodStart = env.getRequiredProperty("riskEvaluation.riskyPeriod.start");
        return LocalTime.parse(periodStart);
    }

    @Bean
    public LocalTime riskyPeriodEnd() {
        String periodEnd = env.getRequiredProperty("riskEvaluation.riskyPeriod.end");
        return LocalTime.parse(periodEnd);
    }

}
