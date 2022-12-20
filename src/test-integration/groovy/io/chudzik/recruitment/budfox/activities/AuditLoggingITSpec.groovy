package io.chudzik.recruitment.budfox.activities

import io.chudzik.recruitment.budfox.BaseITSpec
import io.chudzik.recruitment.budfox.clients.ClientService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Scope
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import spock.lang.Subject
import spock.mock.DetachedMockFactory
import spock.mock.MockFactory

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE

@TestPropertySource(properties = "budfox.audit.enabled=true")
@ContextConfiguration(classes = ActivitiesLoggingITSpecConf)
@Subject([ LogActivityAspect, ActivitiesFacade ])
class AuditLoggingITSpec extends BaseITSpec {

    @Autowired ActivitiesFacade activitiesFacade

    @Autowired ServiceWithLoggedActivity service1
    @Autowired ServiceWithLoggedActivity service2


    def "should log executions of instance methods annotated with @LogActivity"() {
        given:
            assert 0 == activitiesFacade.count()
        and:
            final Long loanId = 44L
        when:
            service1.terminateLoan(loanId)
            service2.terminateLoan(loanId)
            service1.expireLoan(loanId)
            service1.expireLoan(loanId)
            service2.expireLoan(loanId)
        then:
            5 == activitiesFacade.count()

        when: "doesn't works on static methods"
            service1.staticOperationOnLoan(loanId)
        then:
            5 == activitiesFacade.count()

    }


    @TypeChecked
    @CompileStatic
    @Slf4j
    static class ServiceWithLoggedActivity {

        public static final String AUDIT_TAG_LOAN_TERMINATION = 'loan.termination'
        public static final String AUDIT_TAG_LOAN_EXPIRATION = 'loan.expired'
        public static final String AUDIT_TAG_ON_STATIC_METHOD = 'method.static'


        @LogActivity(AUDIT_TAG_ON_STATIC_METHOD)
        static void staticOperationOnLoan(Long loanId) {
            log.debug("Static operation on Loan... [loanId={}]", loanId)
        }


        @LogActivity(AUDIT_TAG_LOAN_EXPIRATION)
        void expireLoan(Long loanId) {
            log.debug("Expiring Loan... [loanId={}]", loanId)
        }

        @LogActivity(AUDIT_TAG_LOAN_TERMINATION)
        void terminateLoan(Long loanId) {
            log.debug("Terminating Loan... [loanId={}]", loanId)
        }

    }


    @Import(ActivitiesConfig)
    @TestConfiguration
    @TypeChecked
    @CompileStatic
    static class ActivitiesLoggingITSpecConf {

        private final MockFactory mockFactory = new DetachedMockFactory()


        @Bean ClientService clientServiceMock() {   // required for ActivitiesConfig
            return mockFactory.Mock(ClientService)
        }

        @ConditionalOnMissingBean(ActivityRepository)
        @Bean
        ActivityRepository inMemoryActivityRepositoryBean() {
            return ActivitiesConfig.inMemoryActivityRepository()
        }


        @Scope(SCOPE_PROTOTYPE)
        @Bean ServiceWithLoggedActivity serviceWithLoggedActivity() {   // required for test logic itself
            return new ServiceWithLoggedActivity()
        }

    }

}
