package io.chudzik.recruitment.budfox.utils;

import io.chudzik.recruitment.budfox.configuration.BudFoxProfiles;
import io.chudzik.recruitment.budfox.configuration.SingletonFixedClockProvider;

public interface BudFoxTestProfiles extends BudFoxProfiles {

    String TEST_INTEGRATION = "test_integration";
    /**
     * @see SingletonFixedClockProvider
     */
    String CLOCK_ADJUSTED = "clock_adjusted";

}
