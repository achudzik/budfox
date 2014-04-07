package me.chudzik.recruitment.vivus.utils.matchers;

import me.chudzik.recruitment.vivus.model.Client;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class ClientAsIntegerMatcher extends TypeSafeMatcher<Integer> {

    private final Client wanted;

    public ClientAsIntegerMatcher(Client wanted) {
        this.wanted = wanted;
    }

    @Override
    public void describeTo(Description description) {
        description.appendValue(wanted.getId());
    }

    @Override
    protected boolean matchesSafely(Integer item) {
        Long actualValue = Long.valueOf(item.longValue());
        return actualValue.equals(wanted.getId());
    }

}
