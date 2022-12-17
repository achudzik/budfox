package io.chudzik.recruitment.budfox.loans

import io.chudzik.recruitment.budfox.BaseClockFixedITSpec
import io.chudzik.recruitment.budfox.BudfoxApplication

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener
import com.github.springtestdbunit.annotation.DatabaseSetup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.SpringBootDependencyInjectionTestExecutionListener
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult

import groovy.json.JsonSlurper

import static org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint.LOG_DEBUG
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK
import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.MONTH_LATER
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.THREE_PLN
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.THREE_WEEKS_PERIOD
import static io.chudzik.recruitment.budfox.utils.PreExistingEntities.VALID_CLIENT

@DatabaseSetup("loanData.xml")
@TestExecutionListeners([
        SpringBootDependencyInjectionTestExecutionListener,
        TransactionDbUnitTestExecutionListener,
])
@AutoConfigureMockMvc(print = LOG_DEBUG)
@SpringBootTest(classes = BudfoxApplication, webEnvironment = MOCK)
class LoanApplicationITSpec extends BaseClockFixedITSpec {

    @Autowired ObjectMapper objectMapper

    @Autowired MockMvc mockMvc


    //FIXME-ach: remove dbunit and validate db content programmatically
    //@ExpectedDatabase(value = "loanData-issue-expected.xml", assertionMode = NON_STRICT_UNORDERED)
    def "should allow taking new loans"() {
        given:
            LoanApplication application = LoanApplication.builder()
                .client(VALID_CLIENT)
                .term(THREE_WEEKS_PERIOD)
                .maturityDate(MONTH_LATER)
                .amount(THREE_PLN)
                .build()

        when:
            MvcResult result = mockMvc.perform(
                post("/loans")
                    .content(objectMapper.writeValueAsBytes(application))
                    .contentType(APPLICATION_JSON)
            )
            //.andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
            .andReturn()
        then:
            result.response.status == CREATED.value()
            result.response.contentType == APPLICATION_JSON_VALUE
            verifyAll (new JsonSlurper().parseText(result.response.contentAsString)) {
                it.id != null
                it.client == VALID_CLIENT.id
                it.conditions.id != null
                it.conditions.loan == it.id
                it.conditions.interest == new BigDecimal("10.0")
                it.conditions.amount == "PLN 3.00"
                it.conditions.maturityDate == MONTH_LATER.toString()
                it.previousConditions == [ ]
            }
    }

}
