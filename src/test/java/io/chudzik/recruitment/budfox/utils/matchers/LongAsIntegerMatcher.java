package io.chudzik.recruitment.budfox.utils.matchers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class LongAsIntegerMatcher extends TypeSafeMatcher<Integer> {

    private final Long wanted;

    public LongAsIntegerMatcher(Long wanted) {
        this.wanted = wanted;
    }

    @Override
    public void describeTo(Description description) {
        description.appendValue(wanted);
    }

    @Override
    protected boolean matchesSafely(Integer item) {
        Long actualValue = Long.valueOf(item.longValue());
        return actualValue.equals(wanted);
    }

}
