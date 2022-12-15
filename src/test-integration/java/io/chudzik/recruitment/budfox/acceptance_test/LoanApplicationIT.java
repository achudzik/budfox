package io.chudzik.recruitment.budfox.acceptance_test;

import io.chudzik.recruitment.budfox.BudfoxApplication;
import io.chudzik.recruitment.budfox.model.LoanApplication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.SqlScriptsTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint.LOG_DEBUG;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
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
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.VALID_CLIENT;
import static io.chudzik.recruitment.budfox.utils.matchers.JsonPathMatchers.hasIdAs;
import static io.chudzik.recruitment.budfox.utils.matchers.JsonPathMatchers.isEqualTo;

@DatabaseSetup("loanData.xml")
@ActiveProfiles({ TEST_INTEGRATION, CLOCK_ADJUSTED })
@TestExecutionListeners({
        TransactionDbUnitTestExecutionListener.class,
        MockitoTestExecutionListener.class,
        SqlScriptsTestExecutionListener.class
})
@AutoConfigureMockMvc(print = LOG_DEBUG)
@SpringBootTest(classes = BudfoxApplication.class, webEnvironment = MOCK)
public class LoanApplicationIT extends AbstractTestNGSpringContextTests {

    @Value("${loan.interest.basic}") BigDecimal interest;

    @Autowired ObjectMapper objectMapper;
    @Autowired MockMvc mockMvc;


    //FIXME-ach: remove dbunit and validate db content programmatically
    //@ExpectedDatabase(value = "loanData-issue-expected.xml", assertionMode = NON_STRICT_UNORDERED)
    @Test
    public void shouldAllowTakingNewLoans() throws Exception {
        // arrange
        LoanApplication application = LoanApplication.builder()
                .client(VALID_CLIENT)
                .term(THREE_WEEKS_PERIOD)
                .maturityDate(MONTH_LATER)
                .amount(THREE_PLN)
                .build();

        // act
        ResultActions result = mockMvc.perform(
                post("/loans")
                    .content(objectMapper.writeValueAsBytes(application))
                    .contentType(APPLICATION_JSON)
                )
                //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
        ;
        // assert
        result
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("id").isNumber())
                .andExpect(jsonPath("client").value(hasIdAs(VALID_CLIENT)))
                .andExpect(jsonPath("conditions.amount").value(isEqualTo(THREE_PLN)))
                .andExpect(jsonPath("conditions.interest").value(isEqualTo(interest)))
                .andExpect(jsonPath("conditions.maturityDate").value(isEqualTo(MONTH_LATER)));
    }

}
