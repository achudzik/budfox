package me.chudzik.recruitment.vivus.web;

import static me.chudzik.recruitment.vivus.utils.JsonUtils.convertObjectToJsonBytes;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.MONTH_LATER;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.THREE_PLN;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.THREE_WEEKS_PERIOD;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.VALID_CLIENT;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.VALID_LOAN;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.VALID_LOAN_APPLICATION;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.YESTERDAY;
import static me.chudzik.recruitment.vivus.utils.matchers.JsonPathMatchers.isEqualTo;
import static me.chudzik.recruitment.vivus.utils.matchers.JsonPathMatchers.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.internal.matchers.NotNull.NOT_NULL;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.servlet.http.HttpServletRequest;

import me.chudzik.recruitment.vivus.configuration.JsonMapperConfiguration;
import me.chudzik.recruitment.vivus.exception.ClientNotFoundException;
import me.chudzik.recruitment.vivus.exception.RiskyLoanApplicationException;
import me.chudzik.recruitment.vivus.model.LoanApplication;
import me.chudzik.recruitment.vivus.model.LoanConditions;
import me.chudzik.recruitment.vivus.service.ActivityService;
import me.chudzik.recruitment.vivus.service.ClientService;
import me.chudzik.recruitment.vivus.service.LoanService;
import me.chudzik.recruitment.vivus.service.RiskAssessmentService;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.HttpMessageConvertersAutoConfiguration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

// TODO-ach: replace all .andExpect(jsonPath("conditions.interest").value(...) with custom assertions
@ContextConfiguration(classes = {JsonMapperConfiguration.class, HttpMessageConvertersAutoConfiguration.class})
public class LoansControllerTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private MappingJackson2HttpMessageConverter messageConverter;

    private MockMvc mockMvc;

    private LoansController sut;

    @Mock
    private ActivityService activityServiceMock;
    @Mock
    private ClientService clientServiceMock;
    @Mock
    private LoanService loanServiceMock;
    @Mock
    private RiskAssessmentService riskAssessmentServiceMock;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
        sut = new LoansController(activityServiceMock, clientServiceMock, loanServiceMock, riskAssessmentServiceMock);
        mockMvc = MockMvcBuilders.standaloneSetup(sut).setMessageConverters(messageConverter).build();
    }

    @Test
    public void shouldThrowExceptionOnInvalidLoanApplicationData() throws Exception {
        // arrange
        LoanApplication applicationWithInvalidMaturityDay = LoanApplication.builder()
                .client(VALID_CLIENT)
                .amount(THREE_PLN)
                .maturityDate(YESTERDAY)
                .term(THREE_WEEKS_PERIOD)
                .build();

        // act
        mockMvc.perform(
                post("/loans")
                    .content(convertObjectToJsonBytes(applicationWithInvalidMaturityDay))
                    .contentType(APPLICATION_JSON))
                //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
        // assert
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("code").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("message").value("Invalid request"))
                .andExpect(jsonPath("details").value(NOT_NULL));
    }

    @Test
    public void shouldThrowExceptionOnNonExistingClient() throws Exception {
        // arrange
        Long nonExistingClientId = new Long(1844);
        LoanApplication applicationWithNonExistingClient = LoanApplication.builder()
                .clientId(nonExistingClientId)
                .amount(THREE_PLN)
                .maturityDate(MONTH_LATER)
                .term(THREE_WEEKS_PERIOD)
                .build();

        doThrow(new ClientNotFoundException(nonExistingClientId))
                .when(clientServiceMock).validateClientExistence(nonExistingClientId);

        // act
        mockMvc.perform(
                post("/loans")
                    .content(convertObjectToJsonBytes(applicationWithNonExistingClient))
                    .contentType(APPLICATION_JSON))
                //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
        // assert
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("code").value(NOT_FOUND.value()))
                .andExpect(jsonPath("message").value("Client with given ID not found."))
                .andExpect(jsonPath("details").value(String.format("Client ID: %d", nonExistingClientId)));
    }

    @Test
    public void shouldLogInformationsAboutLoanApplication() throws Exception {
        // arrange / act 
        mockMvc.perform(
                post("/loans")
                    .content(convertObjectToJsonBytes(VALID_LOAN_APPLICATION))
                    .contentType(APPLICATION_JSON));
                //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print());

        // assert
        verify(activityServiceMock, times(1)).logLoanApplication(
                eq(VALID_LOAN_APPLICATION.getClientId()),
                isA(HttpServletRequest.class));
        verifyNoMoreInteractions(activityServiceMock);
    }

    @Test
    public void shouldNotIssueLoansBasedOnRiskyLoanApplication() throws Exception {
        // arrange
        doThrow(RiskyLoanApplicationException.class)
                .when(riskAssessmentServiceMock)
                .validateApplicationSafety(VALID_LOAN_APPLICATION);

        // act
        mockMvc.perform(
                post("/loans")
                    .content(convertObjectToJsonBytes(VALID_LOAN_APPLICATION))
                    .contentType(APPLICATION_JSON));
                //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print());

        // assert
        verify(riskAssessmentServiceMock, times(1)).validateApplicationSafety(VALID_LOAN_APPLICATION);
        verifyZeroInteractions(loanServiceMock);
    }

    @Test
    public void shouldThrowExceptionOnRiskyLoanApplication() throws Exception {
        // arrange
        String loanRefusalReason = "Max application limit reached.";
        doThrow(new RiskyLoanApplicationException(loanRefusalReason))
                .when(riskAssessmentServiceMock)
                .validateApplicationSafety(VALID_LOAN_APPLICATION);

        // act
        mockMvc.perform(
                post("/loans")
                    .content(convertObjectToJsonBytes(VALID_LOAN_APPLICATION))
                    .contentType(APPLICATION_JSON))
                //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
        // assert
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("code").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("message").value("Risk associated with loan application is too high."))
                .andExpect(jsonPath("details").value(loanRefusalReason));
    }

    @Test
    public void shouldIssueLoanToSafeLoanApplication() throws Exception {
        // arrange
        doReturn(VALID_LOAN).when(loanServiceMock).issueALoan(VALID_LOAN_APPLICATION);
        LoanConditions conditions = VALID_LOAN.getConditions();

        // act
        mockMvc.perform(
                post("/loans")
                    .content(convertObjectToJsonBytes(VALID_LOAN_APPLICATION))
                    .contentType(APPLICATION_JSON))
                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
        // assert
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("id").value(isEqualTo(VALID_LOAN.getId())))
                .andExpect(jsonPath("client").value(hasIdAs(VALID_LOAN.getClient())))
                .andExpect(jsonPath("conditions.interest").value(isEqualTo(conditions.getInterest())))
                .andExpect(jsonPath("conditions.amount").value(isEqualTo(conditions.getAmount())))
                .andExpect(jsonPath("conditions.maturityDate").value(isEqualTo(conditions.getMaturityDate())));

        verify(riskAssessmentServiceMock, times(1)).validateApplicationSafety(VALID_LOAN_APPLICATION);
        verify(loanServiceMock, times(1)).issueALoan(eq(VALID_LOAN_APPLICATION));
    }

}
