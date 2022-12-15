package io.chudzik.recruitment.budfox.risk_evaluator

import io.chudzik.recruitment.budfox.AbstractUnitSpec
import io.chudzik.recruitment.budfox.exception.RiskyLoanApplicationException
import io.chudzik.recruitment.budfox.repository.ActivityRepository
import io.chudzik.recruitment.budfox.service.risk_evaluator.DailyApplicationLimitRiskEvaluator
import org.springframework.mock.web.MockHttpServletRequest
import spock.lang.Subject

import javax.servlet.http.HttpServletRequest

import static io.chudzik.recruitment.budfox.model.Activity.ActivityType.LOAN_APPLICATION
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.VALID_LOAN_APPLICATION

class DailyApplicationLimitRiskEvaluatorSpec extends AbstractUnitSpec {

    static final Integer DAILY_APPLICATION_LIMIT = 2
    static final String IP_ADDRESS = "127.0.0.1"

    ActivityRepository activityRepoMock = Mock()
    HttpServletRequest requestMock = new MockHttpServletRequest().tap {
        it.remoteAddr = IP_ADDRESS
    }

    @Subject def sut = new DailyApplicationLimitRiskEvaluator(activityRepoMock, requestMock, DAILY_APPLICATION_LIMIT)


    def "should check logged application from db"() {
        when:
            sut.evaluate(VALID_LOAN_APPLICATION)
        then:
            1 * activityRepoMock.countByTypeAndIpAddressAndEventTimeAfter(LOAN_APPLICATION, IP_ADDRESS, _) >> 1
    }


    def "should pass last application matching application limit"() {
        given:
            activityRepoMock.countByTypeAndIpAddressAndEventTimeAfter(LOAN_APPLICATION, IP_ADDRESS, _)
                >> DAILY_APPLICATION_LIMIT
        when:
            sut.evaluate(VALID_LOAN_APPLICATION)
        then:
            noExceptionThrown() // no error == it pass
    }


    def "should throw exception on exceeded application limit"() {
        given:
            activityRepoMock.countByTypeAndIpAddressAndEventTimeAfter(LOAN_APPLICATION, IP_ADDRESS, _)
                >> DAILY_APPLICATION_LIMIT + 1
        when:
            sut.evaluate(VALID_LOAN_APPLICATION)
        then:
            RiskyLoanApplicationException ex = thrown()
        and: "should describe rejection reason"
            ex.reason == "Max applications limit per day reached."
    }

}
