package me.chudzik.recruitment.vivus.acceptance_test;

import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.MONTH_AND_A_TWO_WEEKS_LATER;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.MONTH_LATER;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.THREE_PLN;
import static me.chudzik.recruitment.vivus.utils.PreExistingEntities.VALID_CLIENT;
import static me.chudzik.recruitment.vivus.utils.matchers.JsonPathMatchers.hasIdAs;
import static me.chudzik.recruitment.vivus.utils.matchers.JsonPathMatchers.isEqualTo;
import static org.mockito.internal.matchers.NotNull.NOT_NULL;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import me.chudzik.recruitment.vivus.Application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
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
@TestExecutionListeners({
        ServletTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class })
//@DatabaseSetup("loanExtensionData.xml")
public class LoanExtensionIT extends AbstractTestNGSpringContextTests {

    @Qualifier("basicInterest")
    private BigDecimal basicInterest;
    @Qualifier("interestMultiplier")
    private BigDecimal interestMultiplier;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeMethod
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    //@ExpectedDatabase(value = "loanExtensionData-expected.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void shouldAllowExtendingNewLoans() throws Exception {
        // arrange
        Long loanId = 4L;
        BigDecimal interestAfterFirstExtension = basicInterest.multiply(interestMultiplier);
        BigDecimal interestAfterSecondExtension = interestAfterFirstExtension.multiply(interestMultiplier);
        
        // act
        mockMvc.perform(put("/loans/{id}?extend=true", loanId))
                //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
        // assert
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("id").value(NOT_NULL))
                .andExpect(jsonPath("client").value(hasIdAs(VALID_CLIENT)))
                // current conditions
                .andExpect(jsonPath("conditions.amount").value(isEqualTo(THREE_PLN)))
                .andExpect(jsonPath("conditions.interest").value(isEqualTo(interestAfterSecondExtension)))
                .andExpect(jsonPath("conditions.maturityDate").value(isEqualTo(MONTH_AND_A_TWO_WEEKS_LATER)))
                // previous conditions
                .andExpect(jsonPath("previousConditions").isArray())
                // basic conditions
                .andExpect(jsonPath("previousConditions[0].amount").value(isEqualTo(THREE_PLN)))
                .andExpect(jsonPath("previousConditions[0].interest").value(isEqualTo(basicInterest)))
                .andExpect(jsonPath("previousConditions[0].maturityDate").value(isEqualTo(MONTH_LATER)))
                // conditions after first extension
                .andExpect(jsonPath("previousConditions[1].amount").value(isEqualTo(THREE_PLN)))
                .andExpect(jsonPath("previousConditions[1].interest").value(isEqualTo(interestAfterFirstExtension)))
                .andExpect(jsonPath("previousConditions[1].maturityDate").value(isEqualTo(MONTH_LATER)));
    }
}
