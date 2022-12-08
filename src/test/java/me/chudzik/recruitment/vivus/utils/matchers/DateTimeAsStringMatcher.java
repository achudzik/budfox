package me.chudzik.recruitment.vivus.utils.matchers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.joda.time.DateTime;

public class DateTimeAsStringMatcher extends TypeSafeMatcher<String> {
    
    private final DateTime wanted;

    public DateTimeAsStringMatcher(DateTime wanted) {
        this.wanted = wanted;
    }

    @Override
    public void describeTo(Description description) {
        description.appendValue(wanted);
    }

    @Override
    protected boolean matchesSafely(String actual) {
        DateTime actualValue = DateTime.parse(actual);
        // FIXME-ach: workaround to inconsistences between time zones
        return actualValue.isEqual(wanted);
    }

}
