package io.chudzik.recruitment.budfox.utils;

import io.chudzik.recruitment.budfox.commons.SingletonFixedClockProvider;
import io.chudzik.recruitment.budfox.config.BudFoxProfiles;

public interface BudFoxTestProfiles extends BudFoxProfiles {

    String TEST_INTEGRATION = "test_integration";
    /**
     * @see SingletonFixedClockProvider
     */
    String CLOCK_ADJUSTED = "clock_adjusted";

}
