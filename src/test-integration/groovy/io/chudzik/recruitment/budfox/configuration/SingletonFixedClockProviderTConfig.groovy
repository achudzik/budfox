package io.chudzik.recruitment.budfox.configuration


import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile

import javax.validation.ClockProvider

import static io.chudzik.recruitment.budfox.utils.BudFoxTestProfiles.CLOCK_ADJUSTED
import static io.chudzik.recruitment.budfox.utils.BudFoxTestProfiles.TEST_INTEGRATION

@Profile([ TEST_INTEGRATION, CLOCK_ADJUSTED ])
@TestConfiguration
class SingletonFixedClockProviderTConfig {

    /* FIXME-ach: remove after migration to Spring 6+. Left as potentially useful before major test refactor.
    @Bean
    LocalValidatorFactoryBean localValidatorFactoryBean(ClockProvider singletonFixedClockProvider) {
        return new LocalValidatorFactoryBean() {
            @Override
            protected void postProcessConfiguration(Configuration<?> configuration) {
                Optional.of(configuration)
                        .filter(ConfigurationImpl.class::isInstance)
                        .map(ConfigurationImpl.class::cast)
                        .ifPresent(configImpl -> configImpl.clockProvider(singletonFixedClockProvider))
            }
        }

    }
     */


    @Bean
    ClockProvider singletonFixedClockProvider() {
        return new SingletonFixedClockProvider()
    }

}
