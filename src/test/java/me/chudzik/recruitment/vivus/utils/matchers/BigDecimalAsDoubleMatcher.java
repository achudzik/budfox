package me.chudzik.recruitment.vivus.utils.matchers;

import java.math.BigDecimal;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class BigDecimalAsDoubleMatcher extends TypeSafeMatcher<Double> {

    private final BigDecimal wanted;

    public BigDecimalAsDoubleMatcher(BigDecimal wanted) {
        this.wanted = wanted;
    }

    @Override
    protected boolean matchesSafely(Double actual) {
        BigDecimal actualValue = BigDecimal.valueOf(actual);
        return actualValue.compareTo(wanted) == 0;
    }

    @Override
    public void describeTo(Description description) {
        description.appendValue(wanted);
    }

}
