package io.chudzik.recruitment.budfox.configuration;

import org.joda.time.DateTime;

import javax.validation.ClockProvider;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.DEFAULT_ZONE_ID;
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.TODAY;

/**
 * Workaround for failing tests, helping the project to pass the whole build without major refactoring.
 * As for combined support for validation-api 2 in spring < 6 injected into test setup through META-INF/validation.xml.
 * Implemented as singleton to allow possible changing the value in specific test cases.
 */
public class SingletonFixedClockProvider implements ClockProvider {

    private final static ClockProvider CLOCK_PROVIDER_SINGLETON = new FixedClockProvider(TODAY, DEFAULT_ZONE_ID);


    @Override
    public Clock getClock() {
        return CLOCK_PROVIDER_SINGLETON.getClock();
    }


    public static class FixedClockProvider implements ClockProvider {

        private final Clock clock;


        public FixedClockProvider(DateTime dateTime, ZoneId zoneId) {
            Instant today = Instant.ofEpochMilli(dateTime.getMillis());
            clock = Clock.fixed(today, zoneId);
        }


        @Override
        public Clock getClock() {
            return clock;
        }

    }

}
