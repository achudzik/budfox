package io.chudzik.recruitment.budfox.web;

import io.chudzik.recruitment.budfox.BudfoxApplication;
import io.chudzik.recruitment.budfox.configuration.SingletonFixedClockProviderTConfig;
import io.chudzik.recruitment.budfox.exception.ClientException;
import io.chudzik.recruitment.budfox.exception.RiskyLoanApplicationException;
import io.chudzik.recruitment.budfox.model.Loan;
import io.chudzik.recruitment.budfox.model.LoanApplication;
import io.chudzik.recruitment.budfox.model.LoanConditions;
import io.chudzik.recruitment.budfox.service.ActivityService;
import io.chudzik.recruitment.budfox.service.ClientService;
import io.chudzik.recruitment.budfox.service.LoanService;
import io.chudzik.recruitment.budfox.service.RiskAssessmentService;
import io.chudzik.recruitment.budfox.utils.PreExistingEntities;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.DateTime;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint.LOG_DEBUG;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static io.chudzik.recruitment.budfox.utils.BudFoxTestProfiles.CLOCK_ADJUSTED;
import static io.chudzik.recruitment.budfox.utils.BudFoxTestProfiles.TEST_INTEGRATION;
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.MONTH_LATER;
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.THREE_PLN;
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.THREE_WEEKS_PERIOD;
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.TODAY;
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.VALID_CLIENT;
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.VALID_LOAN_APPLICATION;
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.YESTERDAY;
import static io.chudzik.recruitment.budfox.utils.matchers.JsonPathMatchers.hasIdAs;
import static io.chudzik.recruitment.budfox.utils.matchers.JsonPathMatchers.isEqualTo;

// TODO-ach: replace all .andExpect(jsonPath("conditions.interest").value(...) with custom assertions
@ActiveProfiles({ TEST_INTEGRATION, CLOCK_ADJUSTED })
@ContextConfiguration(classes = SingletonFixedClockProviderTConfig.class)
@TestExecutionListeners(MockitoTestExecutionListener.class)
@AutoConfigureMockMvc(print = LOG_DEBUG)
@SpringBootTest(classes = BudfoxApplication.class, webEnvironment = MOCK)
//@WebMvcTest(controllers = LoansController.class)
public class LoansControllerIT extends AbstractTestNGSpringContextTests {

    @MockBean ActivityService activityServiceMock;
    @MockBean ClientService clientServiceMock;
    @MockBean LoanService loanServiceMock;
    @MockBean RiskAssessmentService riskAssessmentServiceMock;

    @Autowired ObjectMapper objectMapper;
    @Autowired MockMvc mockMvc;


    @BeforeMethod
    public void setup() {
        Mockito.reset(activityServiceMock, clientServiceMock, loanServiceMock, riskAssessmentServiceMock);
    }


    @Test
    public void shouldThrowExceptionOnInvalidLoanApplicationData() throws Exception {
        // arrange
        final DateTime invalidDateInThePast = YESTERDAY;
        final LoanApplication applicationWithInvalidMaturityDay = LoanApplication.builder()
                .client(VALID_CLIENT)
                .amount(THREE_PLN)
                .maturityDate(invalidDateInThePast)
                .term(THREE_WEEKS_PERIOD)
                .applicationDate(TODAY)
                .build();

        // act
        ResultActions result = mockMvc.perform(
                post("/loans")
                    .content(objectMapper.writeValueAsBytes(applicationWithInvalidMaturityDay))
                    .contentType(APPLICATION_JSON))
                    //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
        ;
        // assert
        result
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("code").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("message").value("Invalid request"))
                .andExpect(jsonPath("details").value("Field error in object 'loanApplication' on field 'maturityDate': rejected value [2014-04-06T21:37:00.000Z]; codes [Future.loanApplication.maturityDate,Future.maturityDate,Future.org.joda.time.DateTime,Future]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [loanApplication.maturityDate,maturityDate]; arguments []; default message [maturityDate]]; default message [must be a future date]"));
    }


    @Test
    public void shouldThrowExceptionOnNonExistingClient() throws Exception {
        // arrange
        final Long nonExistingClientId = new Long(1844);
        LoanApplication applicationWithNonExistingClient = LoanApplication.builder()
                .clientId(nonExistingClientId)
                .amount(THREE_PLN)
                .maturityDate(MONTH_LATER)
                .term(THREE_WEEKS_PERIOD)
                .build();

        doThrow(ClientException.notFound(nonExistingClientId))
                .when(clientServiceMock).validateClientExistence(nonExistingClientId);

        // act
        ResultActions result = mockMvc.perform(
                post("/loans")
                    .content(objectMapper.writeValueAsBytes(applicationWithNonExistingClient))
                    .contentType(APPLICATION_JSON))
                    //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
        ;
        // assert
        result
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("code").value(NOT_FOUND.value()))
                .andExpect(jsonPath("message").value("Client with given ID not found."))
                .andExpect(jsonPath("details").value(""));
    }


    @Test
    public void shouldLogLoanApplicationActivity() throws Exception {
        // arrange
        final Long clientId = VALID_LOAN_APPLICATION.getClientId();

        // act
        mockMvc.perform(
                post("/loans")
                    .content(objectMapper.writeValueAsBytes(VALID_LOAN_APPLICATION))
                    .contentType(APPLICATION_JSON))
                    //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
        ;
        // assert
        verify(activityServiceMock, times(1)).logLoanApplication(
                eq(clientId),
                isA(HttpServletRequest.class)
        );
    }


    @Test
    public void shouldNotIssueLoansBasedOnRiskyLoanApplication() throws Exception {
        // arrange
        doThrow(RiskyLoanApplicationException.class)
                .when(riskAssessmentServiceMock)
                .validateApplicationSafety(isA(VALID_LOAN_APPLICATION.getClass()));
        // act
        mockMvc.perform(
                post("/loans")
                    .content(objectMapper.writeValueAsBytes(VALID_LOAN_APPLICATION))
                    .contentType(APPLICATION_JSON))
                    //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
        ;
        // assert
        verify(riskAssessmentServiceMock, times(1)).validateApplicationSafety(isA(VALID_LOAN_APPLICATION.getClass()));
        verifyNoInteractions(loanServiceMock);
    }


    @Test
    public void shouldThrowExceptionOnRiskyLoanApplication() throws Exception {
        // arrange
        String loanRefusalReason = "Max application limit reached.";
        doThrow(new RiskyLoanApplicationException(loanRefusalReason))
                .when(riskAssessmentServiceMock)
                .validateApplicationSafety(isA(VALID_LOAN_APPLICATION.getClass()));
        // act
        ResultActions result = mockMvc.perform(
                post("/loans")
                    .content(objectMapper.writeValueAsBytes(VALID_LOAN_APPLICATION))
                    .contentType(APPLICATION_JSON))
                    //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
        ;
        // assert
        result
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("code").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("message").value("Risk associated with loan application is too high."))
                .andExpect(jsonPath("details").value(loanRefusalReason));
    }


    @Test
    public void shouldIssueLoanToSafeLoanApplication() throws Exception {
        // arrange
        final LoanApplication application = PreExistingEntities.loanApplication();
        final Loan loan = PreExistingEntities.loan();
        doReturn(loan).when(loanServiceMock).issueALoan(isA(application.getClass()));
        LoanConditions conditions = loan.getConditions();

        // act
        ResultActions result = mockMvc.perform(
                post("/loans")
                    .content(objectMapper.writeValueAsBytes(application))
                    .contentType(APPLICATION_JSON))
                    //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
        ;
        // assert
        result
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("id").value(isEqualTo(loan.getId())))
                .andExpect(jsonPath("client").value(hasIdAs(loan.getClient())))
                .andExpect(jsonPath("conditions.interest").value(isEqualTo(conditions.getInterest())))
                .andExpect(jsonPath("conditions.amount").value(isEqualTo(conditions.getAmount())))
                .andExpect(jsonPath("conditions.maturityDate").value(isEqualTo(conditions.getMaturityDate())));

        verify(riskAssessmentServiceMock, times(1)).validateApplicationSafety(isA(application.getClass()));
        verify(loanServiceMock, times(1)).issueALoan(isA(application.getClass()));
    }

}
