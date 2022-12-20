package io.chudzik.recruitment.budfox

import io.chudzik.recruitment.budfox.activities.ActivitiesFacade

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.mock.DetachedMockFactory
import spock.mock.MockFactory

import static io.chudzik.recruitment.budfox.commons.tests.BudFoxTestProfiles.TEST_INTEGRATION

@ContextConfiguration(classes = FacadesMockingTConf)
@ActiveProfiles(TEST_INTEGRATION)
abstract class BaseITSpec extends Specification {

    @TestConfiguration
    static class FacadesMockingTConf {

        private final MockFactory mockFactory = new DetachedMockFactory()


        @ConditionalOnMissingBean(ActivitiesFacade)
        @Bean ActivitiesFacade activitiesFacadeMock() {
            return mockFactory.Mock(ActivitiesFacade)
        }

    }

}
