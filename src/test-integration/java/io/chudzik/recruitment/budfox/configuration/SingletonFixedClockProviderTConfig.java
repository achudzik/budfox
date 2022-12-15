package io.chudzik.recruitment.budfox.configuration;

import org.hibernate.validator.internal.engine.ConfigurationImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ClockProvider;
import javax.validation.Configuration;
import java.util.Optional;

import static io.chudzik.recruitment.budfox.utils.BudFoxTestProfiles.CLOCK_ADJUSTED;
import static io.chudzik.recruitment.budfox.utils.BudFoxTestProfiles.TEST_INTEGRATION;

@Profile({ TEST_INTEGRATION, CLOCK_ADJUSTED })
@TestConfiguration
public class SingletonFixedClockProviderTConfig {

    /* FIXME-ach: remove after migration to Spring 6+. Left as potentially useful before major test refactor.
    @Bean
    public LocalValidatorFactoryBean localValidatorFactoryBean(ClockProvider singletonFixedClockProvider) {
        return new LocalValidatorFactoryBean() {
            @Override
            protected void postProcessConfiguration(Configuration<?> configuration) {
                Optional.of(configuration)
                        .filter(ConfigurationImpl.class::isInstance)
                        .map(ConfigurationImpl.class::cast)
                        .ifPresent(configImpl -> configImpl.clockProvider(singletonFixedClockProvider));
            }
        };

    }
     */


    @Bean
    public ClockProvider singletonFixedClockProvider() {
        return new SingletonFixedClockProvider();
    }

}
