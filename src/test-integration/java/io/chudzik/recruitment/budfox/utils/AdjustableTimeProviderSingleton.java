package io.chudzik.recruitment.budfox.utils;

import org.joda.time.DateTime;
import org.joda.time.base.BaseDateTime;

import javax.annotation.Nullable;
import javax.validation.ClockProvider;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;

/**
 * Workaround for failing tests, helping the project to pass the whole build without major refactoring.
 * Done as singleton due to its usage in hibernate-validation (class being instantiated outside of Spring's context,
 * through `META-INF/validation.xml` property).
 */
// FIXME-ach: use ThreadLocal instead of final static
public class AdjustableTimeProviderSingleton implements ClockProvider {

    public final static AdjustableTimeProvider SINGLE_INSTANCE;

    static {
        SINGLE_INSTANCE = new AdjustableTimeProvider();
    }


    public static AdjustableTimeProvider setTo(@Nullable DateTime dateTimeToBeReturned) {
        return SINGLE_INSTANCE.setTo(dateTimeToBeReturned);
    }

    @Override
    public Clock getClock() {
        return SINGLE_INSTANCE.getClock();
    }


    private static class AdjustableTimeProvider implements ClockProvider {

        private Clock fixedClock = toClock(DateTime.now());


        @Override
        public Clock getClock() {
            return fixedClock;
        }


        AdjustableTimeProvider setTo(@Nullable DateTime dateTimeToBeReturned) {
            this.fixedClock = toClock(dateTimeToBeReturned);
            return this;
        }


        private static Clock toClock(DateTime dateTime) {
            return Optional.ofNullable(dateTime)
                .map(BaseDateTime::getMillis)
                .map(Instant::ofEpochMilli)
                .map(fixedInstant -> Clock.fixed(fixedInstant, ZoneId.systemDefault()))
                .orElse(Clock.system(ZoneId.systemDefault()));
        }

    }

}
