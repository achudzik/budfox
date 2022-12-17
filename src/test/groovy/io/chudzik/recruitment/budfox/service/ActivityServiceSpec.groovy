package io.chudzik.recruitment.budfox.service

import io.chudzik.recruitment.budfox.BaseUnitSpec
import io.chudzik.recruitment.budfox.model.Activity
import io.chudzik.recruitment.budfox.repository.ActivityRepository
import io.chudzik.recruitment.budfox.repository.ClientRepository
import io.chudzik.recruitment.budfox.service.impl.ActivityServiceImpl

import org.springframework.mock.web.MockHttpServletRequest
import spock.lang.Subject

import javax.servlet.http.HttpServletRequest

import static io.chudzik.recruitment.budfox.model.Activity.ActivityType.LOAN_APPLICATION
import static io.chudzik.recruitment.budfox.model.Activity.ActivityType.LOAN_EXTENSION
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.LOCAL_IP_ADDRESS
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.client
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.loanApplicationActivity
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.loanExtensionActivity
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.validId

@Subject([ ActivityService, ActivityServiceImpl ])
class ActivityServiceSpec extends BaseUnitSpec {

    ActivityRepository activityRepositoryMock = Mock()
    ClientRepository clientRepositoryMock = Mock()
    HttpServletRequest httpServletRequestMock = new MockHttpServletRequest().tap {
        it.remoteAddr = LOCAL_IP_ADDRESS
    }

    ActivityService sut = new ActivityServiceImpl(activityRepositoryMock, clientRepositoryMock)


    def "should persist info about applying for a loan"() {
        given:
            clientRepositoryMock.getOne(validId()) >> client()
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
            clientRepositoryMock.findByLoansId(validId()) >> client()
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
