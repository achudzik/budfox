package me.chudzik.recruitment.vivus.loan;

import static me.chudzik.recruitment.vivus.utils.JsonUtils.convertObjectToJsonBytes;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.THREE_PLN;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.THREE_WEEKS_PERIOD;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.VALID_CLIENT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import me.chudzik.recruitment.vivus.Application;
import me.chudzik.recruitment.vivus.model.LoanApplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

@WebAppConfiguration
@SpringApplicationConfiguration(classes = Application.class)
@ActiveProfiles("nomanagement")
@TestExecutionListeners({
        ServletTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class })
@DatabaseSetup("loanData.xml")
public class LoanApplicationIT extends AbstractTestNGSpringContextTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeMethod
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @ExpectedDatabase(value = "loanData-issue-expected.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void shouldAllowTakingNewLoans() throws Exception {
        LoanApplication application = LoanApplication.builder()
                .client(VALID_CLIENT)
                .term(THREE_WEEKS_PERIOD)
                .amount(THREE_PLN)
                .build();

        mockMvc.perform(
                post("/loans")
                    .content(convertObjectToJsonBytes(application))
                    .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }

}
