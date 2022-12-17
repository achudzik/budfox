package io.chudzik.recruitment.budfox.acceptance_test

import io.chudzik.recruitment.budfox.BaseClockFixedITSpec
import io.chudzik.recruitment.budfox.BudfoxApplication
import io.chudzik.recruitment.budfox.configuration.BusinessConfiguration

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.SpringBootDependencyInjectionTestExecutionListener
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions

import static org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint.LOG_DEBUG
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.THREE_PLN
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.VALID_CLIENT
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.validId
import static io.chudzik.recruitment.budfox.utils.matchers.JsonPathMatchers.hasIdAs
import static io.chudzik.recruitment.budfox.utils.matchers.JsonPathMatchers.isEqualTo

@DatabaseSetup("loanExtensionData.xml")
@TestExecutionListeners([
        TransactionDbUnitTestExecutionListener,
        SpringBootDependencyInjectionTestExecutionListener,
])
@AutoConfigureMockMvc(print = LOG_DEBUG)
@SpringBootTest(classes = BudfoxApplication, webEnvironment = MOCK)
class LoanExtensionITSpec extends BaseClockFixedITSpec {

    @Autowired BusinessConfiguration businessConf
    @Autowired MockMvc mockMvc


    //FIXME-ach: remove dbunit and validate db content programmatically
    //@ExpectedDatabase(value = "loanExtensionData-expected.xml", assertionMode = NON_STRICT)
    def "should allow extending new loans"() {
        given:
            BigDecimal interestAfterFirstExtension = businessConf.basicInterest() * businessConf.interestMultiplier()
            BigDecimal interestAfterSecondExtension = interestAfterFirstExtension * businessConf.interestMultiplier()

        when:
            ResultActions result = mockMvc.perform(
                            put("/loans/{id}?extend=true", validId())
                    )
                    //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())

        then:
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
                    .andExpect(jsonPath("previousConditions[0].interest", isEqualTo(businessConf.basicInterest())))
//                .andExpect(jsonPath("previousConditions[0].maturityDate", isEqualTo(MONTH_LATER)))

    }

}
