package io.chudzik.recruitment.budfox.web

import io.chudzik.recruitment.budfox.BaseClockFixedITSpec
import io.chudzik.recruitment.budfox.BudfoxApplication
import io.chudzik.recruitment.budfox.exception.ClientException
import io.chudzik.recruitment.budfox.exception.RiskyLoanApplicationException
import io.chudzik.recruitment.budfox.model.Loan
import io.chudzik.recruitment.budfox.model.LoanApplication
import io.chudzik.recruitment.budfox.model.LoanConditions
import io.chudzik.recruitment.budfox.service.ActivityService
import io.chudzik.recruitment.budfox.service.ClientService
import io.chudzik.recruitment.budfox.service.LoanService
import io.chudzik.recruitment.budfox.service.RiskAssessmentService
import io.chudzik.recruitment.budfox.utils.PreExistingEntities

import com.fasterxml.jackson.databind.ObjectMapper
import org.joda.time.DateTime
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions

import javax.servlet.http.HttpServletRequest

import static org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint.LOG_DEBUG
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK
import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.MONTH_LATER
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.THREE_PLN
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.THREE_WEEKS_PERIOD
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.TODAY
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.VALID_CLIENT
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.VALID_LOAN_APPLICATION
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.YESTERDAY
import static io.chudzik.recruitment.budfox.utils.matchers.JsonPathMatchers.hasIdAs
import static io.chudzik.recruitment.budfox.utils.matchers.JsonPathMatchers.isEqualTo

// TODO-ach: replace all .andExpect(jsonPath("conditions.interest").value(...) with custom assertions
@AutoConfigureMockMvc(print = LOG_DEBUG)
@SpringBootTest(classes = BudfoxApplication, webEnvironment = MOCK)
// XXX-ach: redo as @WebMvcTest(controllers = LoansController)
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
            ResultActions result = mockMvc.perform(
                    post("/loans")
                            .content(objectMapper.writeValueAsBytes(applicationWithInvalidMaturityDay))
                            .contentType(APPLICATION_JSON))
            //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())

        then:
            result
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("code").value(BAD_REQUEST.value()))
                    .andExpect(jsonPath("message").value("Invalid request"))
                    .andExpect(jsonPath("details").value("Field error in object 'loanApplication' on field 'maturityDate': rejected value [2014-04-06T21:37:00.000Z]; codes [Future.loanApplication.maturityDate,Future.maturityDate,Future.org.joda.time.DateTime,Future]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [loanApplication.maturityDate,maturityDate]; arguments []; default message [maturityDate]]; default message [must be a future date]"))
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

            clientServiceMock.validateClientExistence(nonExistingClientId)
                    >> { throw ClientException.notFound(nonExistingClientId) }

        when:
            ResultActions result = mockMvc.perform(
                    post("/loans")
                            .content(objectMapper.writeValueAsBytes(applicationWithNonExistingClient))
                            .contentType(APPLICATION_JSON))
            //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())

        then:
            result
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("code").value(NOT_FOUND.value()))
                    .andExpect(jsonPath("message").value("Client with given ID not found."))
                    .andExpect(jsonPath("details").value(""))
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
            ResultActions result = mockMvc.perform(
                    post("/loans")
                            .content(objectMapper.writeValueAsBytes(VALID_LOAN_APPLICATION))
                            .contentType(APPLICATION_JSON))
            //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
        then:
            result
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("code").value(BAD_REQUEST.value()))
                    .andExpect(jsonPath("message").value("Risk associated with loan application is too high."))
                    .andExpect(jsonPath("details").value(loanRefusalReason))
    }


    def "should issue Loan to safe LoanApplication"() {
        given:
            final LoanApplication application = PreExistingEntities.loanApplication()
            final Loan loan = PreExistingEntities.loan()
            LoanConditions conditions = loan.getConditions()

        when:
            ResultActions result = mockMvc.perform(
                    post("/loans")
                            .content(objectMapper.writeValueAsBytes(application))
                            .contentType(APPLICATION_JSON))
            //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())

        then:
            result
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("id").value(isEqualTo(loan.getId())))
                    .andExpect(jsonPath("client").value(hasIdAs(loan.getClient())))
                    .andExpect(jsonPath("conditions.interest").value(isEqualTo(conditions.getInterest())))
                    .andExpect(jsonPath("conditions.amount").value(isEqualTo(conditions.getAmount())))
                    .andExpect(jsonPath("conditions.maturityDate").value(isEqualTo(conditions.getMaturityDate())))
        and:
            1 * riskAssessmentServiceMock.validateApplicationSafety(_ as LoanApplication)
            1 * loanServiceMock.issueALoan(_ as LoanApplication) >> loan
    }

}
