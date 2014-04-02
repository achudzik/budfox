package me.chudzik.recruitment.vivus.web;

import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


public class LoansControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private LoansController sut;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(sut).build();
    }

    @Test
    public void shouldThrowExceptionOnInvalidLoanApplicationData() throws Exception {
        throw new RuntimeException("Test not implemented");
    }

    @Test
    public void shouldLogInformationsAboutLoanApplications() throws Exception {
        throw new RuntimeException("Test not implemented");
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
