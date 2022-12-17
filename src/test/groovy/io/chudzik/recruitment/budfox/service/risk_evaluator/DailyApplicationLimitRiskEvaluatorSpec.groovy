package io.chudzik.recruitment.budfox.service.risk_evaluator

import io.chudzik.recruitment.budfox.BaseUnitSpec
import io.chudzik.recruitment.budfox.activities.ActivityService
import io.chudzik.recruitment.budfox.exception.RiskyLoanApplicationException
import io.chudzik.recruitment.budfox.model.LoanApplication

import org.springframework.mock.web.MockHttpServletRequest
import spock.lang.Rollup
import spock.lang.Subject

import javax.servlet.http.HttpServletRequest

import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.VALID_LOAN_APPLICATION

@Rollup
class DailyApplicationLimitRiskEvaluatorSpec extends BaseUnitSpec {

    static final int DAILY_APPLICATION_LIMIT = 4
    static final String IP_ADDRESS = "127.0.0.1"

    ActivityService activityServiceMock = Mock()
    HttpServletRequest requestMock = new MockHttpServletRequest().tap {
        it.remoteAddr = IP_ADDRESS
    }

    @Subject def sut = new DailyApplicationLimitRiskEvaluator(activityServiceMock, requestMock, DAILY_APPLICATION_LIMIT)


    def "should pass last applications matching application limit"() {
        when:
            sut.evaluate(VALID_LOAN_APPLICATION)
        then:
            1 * activityServiceMock.countLoanApplicationsByIpAddressAndEventTimeAfter(_ as LoanApplication, IP_ADDRESS)
                    >> loanApplicationsCount
        and:
            noExceptionThrown() // no error == it pass
        where:
            loanApplicationsCount << [ 0, 1, DAILY_APPLICATION_LIMIT - 1 ]
    }


    def "should throw exception on limit matching and/or exceeded LoanApplication"() {
        given:
            activityServiceMock.countLoanApplicationsByIpAddressAndEventTimeAfter(_ as LoanApplication, IP_ADDRESS)
                    >> loanApplicationsCount
        when:
            sut.evaluate(VALID_LOAN_APPLICATION)
        then:
            RiskyLoanApplicationException ex = thrown()
        and: "should describe rejection reason"
            ex.reason == "Max applications limit per day reached."
        where:
            loanApplicationsCount << [ DAILY_APPLICATION_LIMIT, DAILY_APPLICATION_LIMIT + 1 ]
    }

}
