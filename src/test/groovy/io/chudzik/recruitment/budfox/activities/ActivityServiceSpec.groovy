package io.chudzik.recruitment.budfox.activities

import io.chudzik.recruitment.budfox.BaseUnitSpec
import io.chudzik.recruitment.budfox.clients.ClientService

import org.springframework.mock.web.MockHttpServletRequest
import spock.lang.Subject

import javax.servlet.http.HttpServletRequest

import static io.chudzik.recruitment.budfox.activities.ActivityType.LOAN_APPLICATION
import static io.chudzik.recruitment.budfox.activities.ActivityType.LOAN_EXTENSION
import static io.chudzik.recruitment.budfox.commons.tests.PreExistingEntities.LOCAL_IP_ADDRESS
import static io.chudzik.recruitment.budfox.commons.tests.PreExistingEntities.client
import static io.chudzik.recruitment.budfox.commons.tests.PreExistingEntities.loanApplicationActivity
import static io.chudzik.recruitment.budfox.commons.tests.PreExistingEntities.loanExtensionActivity
import static io.chudzik.recruitment.budfox.commons.tests.PreExistingEntities.validId

class ActivityServiceSpec extends BaseUnitSpec {

    ActivityRepository activityRepositoryMock = Mock()
    ClientService clientServiceMock = Mock()
    HttpServletRequest httpServletRequestMock = new MockHttpServletRequest().tap {
        it.remoteAddr = LOCAL_IP_ADDRESS
    }

    @Subject def sut = new ActivityService(activityRepositoryMock, clientServiceMock)


    def "should persist info about applying for a loan"() {
        given:
            clientServiceMock.getOne(validId()) >> client()
        when:
            sut.logLoanApplication(validId(), httpServletRequestMock)
        then:
            1 * activityRepositoryMock.save({
                verifyAll (it, Activity) {
                    it.type == LOAN_APPLICATION
                    it.ipAddress == loanApplicationActivity().ipAddress
                    it.eventTime == loanApplicationActivity().eventTime
                }
            })
    }


    def "should persist info about extending loan"() {
        given:
            clientServiceMock.getReferenceId(validId()) >> client()
        when:
            sut.logLoanExtension(validId(), httpServletRequestMock)
        then:
            1 * activityRepositoryMock.save({
                verifyAll (it, Activity) {
                    it.type == LOAN_EXTENSION
                    it.ipAddress == loanExtensionActivity().ipAddress
                    it.eventTime == loanExtensionActivity().eventTime
                }
            })
    }

}
