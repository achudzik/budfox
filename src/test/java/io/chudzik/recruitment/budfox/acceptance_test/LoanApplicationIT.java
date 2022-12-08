package io.chudzik.recruitment.budfox.acceptance_test;

import static io.chudzik.recruitment.budfox.utils.JsonUtils.convertObjectToJsonBytes;
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.MONTH_LATER;
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.THREE_PLN;
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.THREE_WEEKS_PERIOD;
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.VALID_CLIENT;
import static io.chudzik.recruitment.budfox.utils.matchers.JsonPathMatchers.hasIdAs;
import static io.chudzik.recruitment.budfox.utils.matchers.JsonPathMatchers.isEqualTo;
import static org.mockito.internal.matchers.NotNull.NOT_NULL;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import io.chudzik.recruitment.budfox.BudfoxApplication;
import io.chudzik.recruitment.budfox.model.LoanApplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.ServletTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

@WebAppConfiguration
@SpringApplicationConfiguration(classes = BudfoxApplication.class)
@TestExecutionListeners({
        ServletTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionDbUnitTestExecutionListener.class})
@DatabaseSetup("loanData.xml")
public class LoanApplicationIT extends AbstractTestNGSpringContextTests {

    @Value("${loan.interest.basic}")
    private BigDecimal interest;

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
        // arrange
        LoanApplication application = LoanApplication.builder()
                .client(VALID_CLIENT)
                .term(THREE_WEEKS_PERIOD)
                .maturityDate(MONTH_LATER)
                .amount(THREE_PLN)
                .build();

        // act
        mockMvc.perform(
                post("/loans")
                    .content(convertObjectToJsonBytes(application))
                    .contentType(APPLICATION_JSON))
                //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
        // assert
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("id").value(NOT_NULL))
                .andExpect(jsonPath("client").value(hasIdAs(VALID_CLIENT)))
                .andExpect(jsonPath("conditions.amount").value(isEqualTo(THREE_PLN)))
                .andExpect(jsonPath("conditions.interest").value(isEqualTo(interest)))
                .andExpect(jsonPath("conditions.maturityDate").value(isEqualTo(MONTH_LATER)));
    }
}
