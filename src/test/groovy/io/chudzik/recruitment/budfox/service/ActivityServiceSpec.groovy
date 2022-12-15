package io.chudzik.recruitment.budfox.service

import io.chudzik.recruitment.budfox.BaseUnitSpec
import io.chudzik.recruitment.budfox.repository.ActivityRepository
import io.chudzik.recruitment.budfox.repository.ClientRepository
import io.chudzik.recruitment.budfox.service.impl.ActivityServiceImpl
import org.springframework.mock.web.MockHttpServletRequest
import spock.lang.Subject

import javax.servlet.http.HttpServletRequest

import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.*

class ActivityServiceSpec extends BaseUnitSpec {

    ActivityRepository activityRepositoryMock = Mock()
    ClientRepository clientRepositoryMock = Mock()
    HttpServletRequest httpServletRequestMock = new MockHttpServletRequest().tap {
        it.remoteAddr = LOCAL_IP_ADDRESS
    }

    @Subject def sut = new ActivityServiceImpl(activityRepositoryMock, clientRepositoryMock)


    def "should persist info about applying for a loan"() {
        given:
            clientRepositoryMock.getOne(validId()) >> client()
        when:
            sut.logLoanApplication(validId(), httpServletRequestMock)
        then:
            1 * activityRepositoryMock.save(loanApplicationActivity())
    }


    def "should persist info about extending loan"() {
        given:
            clientRepositoryMock.findByLoansId(invalidId()) >> client()
        when:
            sut.logLoanExtension(invalidId(), httpServletRequestMock)
        then:
            1 * activityRepositoryMock.save(loanExtensionActivity())
    }

}
