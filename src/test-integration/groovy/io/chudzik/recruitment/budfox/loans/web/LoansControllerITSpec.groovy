package io.chudzik.recruitment.budfox.loans.web

import io.chudzik.recruitment.budfox.BaseClockFixedITSpec
import io.chudzik.recruitment.budfox.BudfoxApplication
import io.chudzik.recruitment.budfox.activities.ActivityService
import io.chudzik.recruitment.budfox.clients.ClientService
import io.chudzik.recruitment.budfox.clients.dto.ClientException
import io.chudzik.recruitment.budfox.clients.web.ClientExceptionHandler
import io.chudzik.recruitment.budfox.commons.tests.PreExistingEntities
import io.chudzik.recruitment.budfox.loans.Loan
import io.chudzik.recruitment.budfox.loans.LoanApplication
import io.chudzik.recruitment.budfox.loans.LoanConditions
import io.chudzik.recruitment.budfox.loans.LoanService
import io.chudzik.recruitment.budfox.loans.RiskAssessmentService
import io.chudzik.recruitment.budfox.loans.dto.RiskyLoanApplicationException

import com.fasterxml.jackson.databind.ObjectMapper
import org.joda.time.DateTime
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Subject

import groovy.json.JsonSlurper
import javax.servlet.http.HttpServletRequest

import static org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint.LOG_DEBUG
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK
import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

import static io.chudzik.recruitment.budfox.commons.tests.PreExistingEntities.MONTH_LATER
import static io.chudzik.recruitment.budfox.commons.tests.PreExistingEntities.THREE_PLN
import static io.chudzik.recruitment.budfox.commons.tests.PreExistingEntities.THREE_WEEKS_PERIOD
import static io.chudzik.recruitment.budfox.commons.tests.PreExistingEntities.TODAY
import static io.chudzik.recruitment.budfox.commons.tests.PreExistingEntities.VALID_CLIENT
import static io.chudzik.recruitment.budfox.commons.tests.PreExistingEntities.VALID_LOAN_APPLICATION
import static io.chudzik.recruitment.budfox.commons.tests.PreExistingEntities.YESTERDAY

// TODO-ach: replace all .andExpect(jsonPath("conditions.interest").value(...) with custom assertions
@ContextConfiguration(classes = ClientExceptionHandler)
@AutoConfigureMockMvc(print = LOG_DEBUG)
@SpringBootTest(classes = BudfoxApplication, webEnvironment = MOCK)
// XXX-ach: redo as @WebMvcTest(controllers = LoansController)
@Subject(LoansController)
class LoansControllerITSpec extends BaseClockFixedITSpec {

    @SpringBean ActivityService activityServiceMock = Mock()
    @SpringBean ClientService ClientServiceMock = Mock()
    @SpringBean LoanService loanServiceMock = Mock()
    @SpringBean RiskAssessmentService riskAssessmentServiceMock = Mock()

    @Autowired ObjectMapper objectMapper
    @Autowired MockMvc mockMvc


    def "should throw exception on invalid LoanApplication data"() {
        given:
            final DateTime invalidDateInThePast = YESTERDAY
            final LoanApplication applicationWithInvalidMaturityDay = LoanApplication.builder()
                    .client(VALID_CLIENT)
                    .amount(THREE_PLN)
                    .maturityDate(invalidDateInThePast)
                    .term(THREE_WEEKS_PERIOD)
                    .applicationDate(TODAY)
                    .build()

        when:
            MockHttpServletResponse response = mockMvc.perform(
                            post("/loans")
                                    .content(objectMapper.writeValueAsBytes(applicationWithInvalidMaturityDay))
                                    .contentType(APPLICATION_JSON))
                    //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                    .andReturn().response
        then:
            response.status == BAD_REQUEST.value()
            response.contentType == APPLICATION_JSON_VALUE
            verifyAll (new JsonSlurper().parseText(response.contentAsString)) {
                it.code == BAD_REQUEST.value()
                it.message == 'Invalid request'
                it.details == 'Field error in object \'loanApplication\' on field \'maturityDate\': rejected value [2014-04-06T21:37:00.000Z]; codes [Future.loanApplication.maturityDate,Future.maturityDate,Future.org.joda.time.DateTime,Future]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [loanApplication.maturityDate,maturityDate]; arguments []; default message [maturityDate]]; default message [must be a future date]'
            }
    }


    def "should throw exception on non existing Client"() {
        given:
            final Long nonExistingClientId = Long.valueOf(1844)
            LoanApplication applicationWithNonExistingClient = LoanApplication.builder()
                    .clientId(nonExistingClientId)
                    .amount(THREE_PLN)
                    .maturityDate(MONTH_LATER)
                    .term(THREE_WEEKS_PERIOD)
                    .build()
        and:
            clientServiceMock.validateClientExistence(_ as Long)
                    >> { throw ClientException.notFound(nonExistingClientId) }
        when:
            MockHttpServletResponse response = mockMvc.perform(
                            post("/loans")
                                    .content(objectMapper.writeValueAsBytes(applicationWithNonExistingClient))
                                    .contentType(APPLICATION_JSON)
                    )
                    //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                    .andReturn().response
        then:
            response.status == NOT_FOUND.value()
            response.contentType == APPLICATION_JSON_VALUE
            verifyAll (new JsonSlurper().parseText(response.contentAsString)) {
                it.code == NOT_FOUND.value()
                it.message == 'Client with given ID not found.'
                it.details == null
            }
    }


    def "should log LoanApplication activity"() {
        given:
            final Long clientId = VALID_LOAN_APPLICATION.getClientId()
        when:
            mockMvc.perform(
                    post("/loans")
                            .content(objectMapper.writeValueAsBytes(VALID_LOAN_APPLICATION))
                            .contentType(APPLICATION_JSON))
            //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
        then:
            1 * activityServiceMock.logLoanApplication(clientId, _ as HttpServletRequest)
    }


    def "should not issue loans based on risky LoanApplication"() {
        given:
            riskAssessmentServiceMock.validateApplicationSafety(_ as LoanApplication)
                    >> { throw new RiskyLoanApplicationException("mocked") }
        when:
            mockMvc.perform(
                    post("/loans")
                            .content(objectMapper.writeValueAsBytes(VALID_LOAN_APPLICATION))
                            .contentType(APPLICATION_JSON))
            //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())

        then:
            0 * loanServiceMock.issueALoan(_ as LoanApplication)
    }


    def "should throw exception on risky LoanApplication"() {
        given:
            final String loanRefusalReason = "Max application limit reached."
            riskAssessmentServiceMock.validateApplicationSafety(_ as LoanApplication)
                    >> { throw new RiskyLoanApplicationException(loanRefusalReason) }
        when:
            MockHttpServletResponse response = mockMvc.perform(
                            post("/loans")
                                    .content(objectMapper.writeValueAsBytes(VALID_LOAN_APPLICATION))
                                    .contentType(APPLICATION_JSON)
                    )
                    //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                    .andReturn().response
        then:
            response.status == BAD_REQUEST.value()
            response.contentType == APPLICATION_JSON_VALUE
            verifyAll (new JsonSlurper().parseText(response.contentAsString)) {
                it.code == BAD_REQUEST.value()
                it.message == 'Risk associated with loan application is too high.'
                it.details == loanRefusalReason
            }
    }


    def "should issue Loan to safe LoanApplication"() {
        given:
            final LoanApplication application = PreExistingEntities.loanApplication()
            final Loan loan = PreExistingEntities.loan()
            LoanConditions conditions = loan.getConditions()

        when:
            MockHttpServletResponse response = mockMvc.perform(
                            post("/loans")
                                    .content(objectMapper.writeValueAsBytes(application))
                                    .contentType(APPLICATION_JSON)
                    )
                    //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                    .andReturn().response
        then:
            response.status == CREATED.value()
            response.contentType == APPLICATION_JSON_VALUE
            verifyAll (new JsonSlurper().parseText(response.contentAsString)) {
                it.id == loan.getId()
                it.client == loan.client.id
                it.conditions.interest == conditions.getInterest()
                it.conditions.amount == 'PLN 3.00'
                it.conditions.maturityDate == conditions.getMaturityDate().toString()
            }
        and:
            1 * riskAssessmentServiceMock.validateApplicationSafety(_ as LoanApplication)
            1 * loanServiceMock.issueALoan(_ as LoanApplication) >> loan
    }

}
