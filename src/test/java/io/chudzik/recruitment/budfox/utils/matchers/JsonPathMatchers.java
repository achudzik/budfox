package io.chudzik.recruitment.budfox.utils.matchers;

import io.chudzik.recruitment.budfox.model.Client;

import org.joda.money.Money;
import org.joda.time.DateTime;

import java.math.BigDecimal;

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

    public static ClientAsIntegerMatcher hasIdAs(Client wanted) {
        return new ClientAsIntegerMatcher(wanted);
    }

    public static LongAsIntegerMatcher isEqualTo(Long wanted) {
        return new LongAsIntegerMatcher(wanted);
    }

}
