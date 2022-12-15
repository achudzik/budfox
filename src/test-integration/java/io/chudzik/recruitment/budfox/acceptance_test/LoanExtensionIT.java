package io.chudzik.recruitment.budfox.acceptance_test;

import io.chudzik.recruitment.budfox.BudfoxApplication;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static io.chudzik.recruitment.budfox.utils.BudFoxTestProfiles.CLOCK_ADJUSTED;
import static io.chudzik.recruitment.budfox.utils.BudFoxTestProfiles.TEST_INTEGRATION;
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.THREE_PLN;
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.VALID_CLIENT;
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.validId;
import static io.chudzik.recruitment.budfox.utils.matchers.JsonPathMatchers.hasIdAs;
import static io.chudzik.recruitment.budfox.utils.matchers.JsonPathMatchers.isEqualTo;

@DatabaseSetup("loanExtensionData.xml")
@ActiveProfiles({ TEST_INTEGRATION, CLOCK_ADJUSTED })
@TestExecutionListeners({
        TransactionDbUnitTestExecutionListener.class,
        MockitoTestExecutionListener.class,
        SqlScriptsTestExecutionListener.class
})
@AutoConfigureMockMvc(print = LOG_DEBUG)
@SpringBootTest(classes = BudfoxApplication.class, webEnvironment = MOCK)
public class LoanExtensionIT extends AbstractTestNGSpringContextTests {

    @Qualifier("basicInterest")
    @Autowired BigDecimal basicInterest;
    @Qualifier("interestMultiplier")
    @Autowired BigDecimal interestMultiplier;

    @Autowired MockMvc mockMvc;


    //@ExpectedDatabase(value = "loanExtensionData-expected.xml", assertionMode = NON_STRICT)
    @Test
    public void shouldAllowExtendingNewLoans() throws Exception {
        // arrange
        BigDecimal interestAfterFirstExtension = basicInterest.multiply(interestMultiplier);
        BigDecimal interestAfterSecondExtension = interestAfterFirstExtension.multiply(interestMultiplier);

        // act
        ResultActions result = mockMvc.perform(put("/loans/{id}?extend=true", validId()))
                //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
        ;
        // assert
        result
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("id").value(validId()))
                .andExpect(jsonPath("client", hasIdAs(VALID_CLIENT)))
                // current conditions
                .andExpect(jsonPath("conditions.amount", isEqualTo(THREE_PLN)))
                .andExpect(jsonPath("conditions.interest", isEqualTo(interestAfterSecondExtension)))
//                .andExpect(jsonPath("conditions.maturityDate", isEqualTo(MONTH_AND_A_TWO_WEEKS_LATER)))
                // previous conditions
                .andExpect(jsonPath("previousConditions").isArray())
                // basic conditions
                .andExpect(jsonPath("previousConditions[1].amount", isEqualTo(THREE_PLN)))
                .andExpect(jsonPath("previousConditions[1].interest", isEqualTo(interestAfterFirstExtension)))
//                .andExpect(jsonPath("previousConditions[1].maturityDate", isEqualTo(MONTH_AND_A_WEEK_LATER)))
                // conditions after first extension
                .andExpect(jsonPath("previousConditions[0].amount", isEqualTo(THREE_PLN)))
                .andExpect(jsonPath("previousConditions[0].interest", isEqualTo(basicInterest)))
//                .andExpect(jsonPath("previousConditions[0].maturityDate", isEqualTo(MONTH_LATER)))
        ;
    }

}
