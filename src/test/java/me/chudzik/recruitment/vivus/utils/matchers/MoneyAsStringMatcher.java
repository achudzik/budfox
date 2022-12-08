package me.chudzik.recruitment.vivus.utils.matchers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.joda.money.Money;

public class MoneyAsStringMatcher extends TypeSafeMatcher<String> {
    
    private final Money wanted;

    public MoneyAsStringMatcher(Money wanted) {
        this.wanted = wanted;
    }

    @Override
    public void describeTo(Description description) {
        description.appendValue(wanted);
    }

    @Override
    protected boolean matchesSafely(String actual) {
        Money actualValue = Money.parse(actual);
        return actualValue.isEqual(wanted);
    }

}
