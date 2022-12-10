package io.chudzik.recruitment.budfox.web;

import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.TODAY;
import static io.chudzik.recruitment.budfox.utils.matchers.JsonPathMatchers.isEqualTo;
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
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.servlet.http.HttpServletRequest;

import io.chudzik.recruitment.budfox.configuration.ControllerTestConfiguration;
import io.chudzik.recruitment.budfox.exception.ClientNotFoundException;
import io.chudzik.recruitment.budfox.exception.RiskyLoanApplicationException;
import io.chudzik.recruitment.budfox.model.Loan;
import io.chudzik.recruitment.budfox.model.LoanApplication;
import io.chudzik.recruitment.budfox.model.LoanConditions;
import io.chudzik.recruitment.budfox.service.ActivityService;
import io.chudzik.recruitment.budfox.service.ClientService;
import io.chudzik.recruitment.budfox.service.LoanService;
import io.chudzik.recruitment.budfox.service.RiskAssessmentService;
import io.chudzik.recruitment.budfox.utils.AdjustableTimeProviderSingleton;
import io.chudzik.recruitment.budfox.utils.JsonUtils;
import io.chudzik.recruitment.budfox.utils.PreExistingEntities;
import io.chudzik.recruitment.budfox.utils.matchers.JsonPathMatchers;

import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

// TODO-ach: replace all .andExpect(jsonPath("conditions.interest").value(...) with custom assertions
@ContextConfiguration(classes = ControllerTestConfiguration.class)
public class LoansControllerIT extends AbstractTestNGSpringContextTests {

    @Autowired
    private MappingJackson2HttpMessageConverter messageConverter;
    @Autowired
    private ExceptionHandlerExceptionResolver exceptionResolver;

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
        mockMvc = MockMvcBuilders.standaloneSetup(sut)
                .setHandlerExceptionResolvers(exceptionResolver)
                .setMessageConverters(messageConverter)
                .build();
        AdjustableTimeProviderSingleton.setTo(TODAY);
    }

    @Test
    public void shouldThrowExceptionOnInvalidLoanApplicationData() throws Exception {
        // arrange
        LoanApplication applicationWithInvalidMaturityDay = LoanApplication.builder()
                .client(PreExistingEntities.VALID_CLIENT)
                .amount(PreExistingEntities.THREE_PLN)
                .maturityDate(PreExistingEntities.YESTERDAY)
                .term(PreExistingEntities.THREE_WEEKS_PERIOD)
                .build();

        // act
        mockMvc.perform(
                post("/loans")
                    .content(JsonUtils.convertObjectToJsonBytes(applicationWithInvalidMaturityDay))
                    .contentType(APPLICATION_JSON))
                //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
        // assert
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
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
                .amount(PreExistingEntities.THREE_PLN)
                .maturityDate(PreExistingEntities.MONTH_LATER)
                .term(PreExistingEntities.THREE_WEEKS_PERIOD)
                .build();

        Mockito.doThrow(new ClientNotFoundException(nonExistingClientId))
                .when(clientServiceMock).validateClientExistence(nonExistingClientId);

        // act
        mockMvc.perform(
                post("/loans")
                    .content(JsonUtils.convertObjectToJsonBytes(applicationWithNonExistingClient))
                    .contentType(APPLICATION_JSON))
                //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
        // assert
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("code").value(NOT_FOUND.value()))
                .andExpect(jsonPath("message").value("Client with given ID not found."))
                .andExpect(jsonPath("details").value(String.format("Client ID: %d", nonExistingClientId)));
    }

    @Test
    public void shouldLogInformationsAboutLoanApplication() throws Exception {
        // arrange / act 
        mockMvc.perform(
                post("/loans")
                    .content(JsonUtils.convertObjectToJsonBytes(PreExistingEntities.VALID_LOAN_APPLICATION))
                    .contentType(APPLICATION_JSON));
                //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print());

        // assert
        verify(activityServiceMock, times(1)).logLoanApplication(
                Matchers.eq(PreExistingEntities.VALID_LOAN_APPLICATION.getClientId()),
                isA(HttpServletRequest.class));
        verifyNoMoreInteractions(activityServiceMock);
    }

    @Test
    public void shouldNotIssueLoansBasedOnRiskyLoanApplication() throws Exception {
        // arrange
        Mockito.doThrow(RiskyLoanApplicationException.class)
                .when(riskAssessmentServiceMock)
                .validateApplicationSafety(PreExistingEntities.VALID_LOAN_APPLICATION);

        // act
        mockMvc.perform(
                post("/loans")
                    .content(JsonUtils.convertObjectToJsonBytes(PreExistingEntities.VALID_LOAN_APPLICATION))
                    .contentType(APPLICATION_JSON));
                //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print());

        // assert
        verify(riskAssessmentServiceMock, times(1)).validateApplicationSafety(PreExistingEntities.VALID_LOAN_APPLICATION);
        verifyZeroInteractions(loanServiceMock);
    }

    @Test
    public void shouldThrowExceptionOnRiskyLoanApplication() throws Exception {
        // arrange
        String loanRefusalReason = "Max application limit reached.";
        doThrow(new RiskyLoanApplicationException(loanRefusalReason))
                .when(riskAssessmentServiceMock)
                .validateApplicationSafety(PreExistingEntities.VALID_LOAN_APPLICATION);

        // act
        mockMvc.perform(
                post("/loans")
                    .content(JsonUtils.convertObjectToJsonBytes(PreExistingEntities.VALID_LOAN_APPLICATION))
                    .contentType(APPLICATION_JSON))
                //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
        // assert
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("code").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("message").value("Risk associated with loan application is too high."))
                .andExpect(jsonPath("details").value(loanRefusalReason));
    }

    @Test
    public void shouldIssueLoanToSafeLoanApplication() throws Exception {
        // arrange
        Loan loan = PreExistingEntities.loan();
        LoanApplication application = PreExistingEntities.loanApplication();

        doReturn(loan).when(loanServiceMock).issueALoan(application);
        LoanConditions conditions = loan.getConditions();

        // act
        mockMvc.perform(
                post("/loans")
                    .content(JsonUtils.convertObjectToJsonBytes(application))
                    .contentType(APPLICATION_JSON))
                //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
        // assert
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("id").value(JsonPathMatchers.isEqualTo(loan.getId())))
                .andExpect(jsonPath("client").value(JsonPathMatchers.hasIdAs(loan.getClient())))
                .andExpect(jsonPath("conditions.interest").value(JsonPathMatchers.isEqualTo(conditions.getInterest())))
                .andExpect(jsonPath("conditions.amount").value(JsonPathMatchers.isEqualTo(conditions.getAmount())))
                .andExpect(jsonPath("conditions.maturityDate").value(JsonPathMatchers.isEqualTo(conditions.getMaturityDate())));

        verify(riskAssessmentServiceMock, times(1)).validateApplicationSafety(application);
        verify(loanServiceMock, times(1)).issueALoan(application);
    }

}
