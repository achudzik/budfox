package me.chudzik.recruitment.vivus.web;

import static me.chudzik.recruitment.vivus.utils.JsonUtils.convertObjectToJsonBytes;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.THREE_PLN;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.THREE_WEEKS_PERIOD;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.VALID_CLIENT;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.VALID_LOAN_APPLICATION;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.YESTERDAY;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.matchers.NotNull.NOT_NULL;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.servlet.http.HttpServletRequest;

import me.chudzik.recruitment.vivus.configuration.JsonMapperConfiguration;
import me.chudzik.recruitment.vivus.model.LoanApplication;
import me.chudzik.recruitment.vivus.service.ActivityService;

import org.mockito.InjectMocks;
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

@ContextConfiguration(classes = {JsonMapperConfiguration.class, HttpMessageConvertersAutoConfiguration.class})
public class LoansControllerTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private MappingJackson2HttpMessageConverter messageConverter;

    private MockMvc mockMvc;

    @InjectMocks
    private LoansController sut;

    @Mock
    private ActivityService activityService;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(sut).setMessageConverters(messageConverter).build();
    }

    @Test
    public void shouldThrowDetailedExceptionOnInvalidLoanApplicationData() throws Exception {
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
    public void shouldLogInformationsAboutLoanApplication() throws Exception {
        // arrange / act 
        mockMvc.perform(
                post("/loans")
                    .content(convertObjectToJsonBytes(VALID_LOAN_APPLICATION))
                    .contentType(APPLICATION_JSON));

        // assert 
        verify(activityService).logLoanApplication(
                eq(VALID_LOAN_APPLICATION.getClientId()),
                isA(HttpServletRequest.class));
    }

    @Test
    public void shouldNotIssueLoansBasedOnRiskyLoanApplication() {
        throw new RuntimeException("Test not implemented");
    }

    @Test
    public void shouldThrowExceptionOnRiskyLoanApplication() {
        throw new RuntimeException("Test not implemented");
    }

    @Test
    public void shouldIssueLoanToSafeLoanApplication() {
        throw new RuntimeException("Test not implemented");
    }

}
