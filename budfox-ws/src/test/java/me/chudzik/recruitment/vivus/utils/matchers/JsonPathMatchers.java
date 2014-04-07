package me.chudzik.recruitment.vivus.utils.matchers;

import java.math.BigDecimal;

import me.chudzik.recruitment.vivus.model.Client;

import org.joda.money.Money;
import org.joda.time.DateTime;

/**
 * Collection of matchers prepared to use with JsonPath. Without proper configuration (like in Jackson),
 * some types are resolved to invalid datatypes (Integer instead of Long, String instead of DataTime, etc.).
 *
 */
// XXX-ach: replace with proper configuration of JsonPath (don't even know if it's possible) / custom fest assertion
public class JsonPathMatchers {

    public static BigDecimalAsDoubleMatcher isEqualTo(BigDecimal wanted) {
        return new BigDecimalAsDoubleMatcher(wanted);
    }

    public static DateTimeAsStringMatcher isEqualTo(DateTime wanted) {
        return new DateTimeAsStringMatcher(wanted);
    }

    public static MoneyAsStringMatcher isEqualTo(Money wanted) {
        return new MoneyAsStringMatcher(wanted);
    }

    public static ClientAsIntegerMatcher isIdAsHas(Client wanted) {
        return new ClientAsIntegerMatcher(wanted);
    }

}
