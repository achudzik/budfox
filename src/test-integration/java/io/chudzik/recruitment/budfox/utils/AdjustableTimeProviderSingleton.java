package io.chudzik.recruitment.budfox.utils;

import org.hibernate.validator.spi.time.TimeProvider;
import org.joda.time.DateTime;

/**
 * Workaround for failing tests, helping the project to pass the whole build without major refactoring.
 * Done as singleton due to its usage in hibernate-validation (class being instantiated outside of Spring's context,
 * through `META-INF/validation.xml` property).
 */
// FIXME-ach: use ThreadLocal instead of final static
public class AdjustableTimeProviderSingleton implements TimeProvider {

    public final static AdjustableTimeProvider SINGLE_INSTANCE;

    static {
        SINGLE_INSTANCE = new AdjustableTimeProvider();
    }


    @Override
    public long getCurrentTime() {
        return SINGLE_INSTANCE.getCurrentTime();
    }


    public static long setTo(DateTime dateTimeToBeReturned) {
        return SINGLE_INSTANCE.setTo(dateTimeToBeReturned);
    }


    private static class AdjustableTimeProvider implements TimeProvider {

        private DateTime fixedDateTime = DateTime.now();


        @Override
        public long getCurrentTime() {
            return fixedDateTime.getMillis();
        }


        long setTo(DateTime dateTimeToBeReturned) {
            this.fixedDateTime = dateTimeToBeReturned;
            return getCurrentTime();
        }

    }

}
